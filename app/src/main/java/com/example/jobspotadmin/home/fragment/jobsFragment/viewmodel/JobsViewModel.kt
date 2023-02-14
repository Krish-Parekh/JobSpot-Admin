package com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Job
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.example.jobspotadmin.util.Constants.Companion.COMPANY_IMAGE_STORAGE_PATH
import com.example.jobspotadmin.util.Resource
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "JobsViewModelTAG"

class JobsViewModel : ViewModel() {

    private var imageUri: Uri? = null
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val mRealtimeDb: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private var companyListener : ListenerRegistration? = null

    private val _uploadJobStatus : MutableLiveData<Resource<String>> = MutableLiveData()
    val uploadJobStatus : LiveData<Resource<String>> = _uploadJobStatus

    private val _deleteJobStatus: MutableLiveData<Resource<String>> = MutableLiveData()
    val deleteJobStatus: LiveData<Resource<String>> = _deleteJobStatus

    private val _jobs: MutableLiveData<Resource<List<Job>>> = MutableLiveData()
    val jobs: LiveData<Resource<List<Job>>> = _jobs

    fun setImageUri(imageUri: Uri?) {
        this@JobsViewModel.imageUri = imageUri
    }

    fun getImageUri(): Uri? {
        return this@JobsViewModel.imageUri
    }


    fun uploadData(imageUri: Uri, job: Job) {
        viewModelScope.launch {
            try {
                _uploadJobStatus.postValue(Resource.loading())
                val uid = job.uid
                val jobImagePath = "$COMPANY_IMAGE_STORAGE_PATH/$uid"
                val imageRef = mStorage.reference.child(jobImagePath)
                imageRef.putFile(imageUri).await()
                val imageUrl = imageRef.downloadUrl.await().toString()
                job.imageUrl = imageUrl
                val companyRef = mFirestore.collection(COLLECTION_PATH_COMPANY).document(uid)
                companyRef.set(job).await()
                _uploadJobStatus.postValue(Resource.success("Job upload success."))
            } catch (error: Exception) {
                val errorMessage = error.message!!
                _uploadJobStatus.postValue(Resource.error(errorMessage))
            }
        }
    }

    fun fetchJobs() {
        viewModelScope.launch(IO) {
            try {
                _jobs.postValue(Resource.loading())
                val companyRef = mFirestore.collection(COLLECTION_PATH_COMPANY).orderBy("uid", Query.Direction.DESCENDING)
                companyListener = companyRef.addSnapshotListener { value, error ->
                    if (error != null) {
                        val errorMessage = error.message!!
                        _jobs.postValue(Resource.error(errorMessage))
                        return@addSnapshotListener
                    }
                    val jobDocuments = value!!.documents
                    val jobs = jobDocuments.map { document ->
                        document.toObject(Job::class.java)!!
                    }
                    _jobs.postValue(Resource.success(jobs))
                }
            } catch (error: Exception) {
                val errorMessage = error.message!!
                _jobs.postValue(Resource.error(errorMessage))
            }
        }
    }

    fun deleteJob(job: Job) {
        viewModelScope.launch(IO) {
            try {
                _deleteJobStatus.postValue(Resource.loading())

                val companyId = job.uid
                val companyImagePath = "$COMPANY_IMAGE_STORAGE_PATH/$companyId"
                val companyDatabasePath = "$COLLECTION_PATH_COMPANY/$companyId"
                val studentCompanyDatabasePath = "$COLLECTION_PATH_COMPANY/$companyId"

                mFirestore.collection(COLLECTION_PATH_COMPANY).document(companyId).delete().await()
                mStorage.reference.child(companyImagePath).delete().await()

                mRealtimeDb.child(companyDatabasePath).removeValue().await()

                val deleteCompaniesFromStudentDeferred = CompletableDeferred<Unit>()
                val studentRef = mRealtimeDb.child(COLLECTION_PATH_STUDENT)
                val companyDeleteListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { studentNode ->
                            val doCompanyExist = studentNode.hasChild(studentCompanyDatabasePath)
                            if (doCompanyExist) {
                                studentNode.child(studentCompanyDatabasePath).ref.removeValue()
                            }
                        }
                        deleteCompaniesFromStudentDeferred.complete(Unit)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        deleteCompaniesFromStudentDeferred.completeExceptionally(error.toException())
                    }
                }
                studentRef.addValueEventListener(companyDeleteListener)
                deleteCompaniesFromStudentDeferred.invokeOnCompletion { error ->
                    if (error != null) {
                        val errorMessage = error.message!!
                        _deleteJobStatus.postValue(Resource.error(errorMessage))
                    }
                    studentRef.removeEventListener(companyDeleteListener)
                }
                deleteCompaniesFromStudentDeferred.await()
                _deleteJobStatus.postValue(Resource.success("Job delete success."))

            } catch (error: Exception) {
                val errorMessage = error.message!!
                _deleteJobStatus.postValue(Resource.error(errorMessage))
            }
        }
    }

    override fun onCleared() {
        companyListener?.remove()
        super.onCleared()
    }
}