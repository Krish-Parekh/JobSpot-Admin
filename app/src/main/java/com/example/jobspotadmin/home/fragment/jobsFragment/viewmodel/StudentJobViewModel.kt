package com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.HostNotification
import com.example.jobspotadmin.model.JobApplication
import com.example.jobspotadmin.model.JobStatus
import com.example.jobspotadmin.model.Student
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_NOTIFICATION
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "StudentJobViewModelTAG"

class StudentJobViewModel : ViewModel() {
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mRealtimeDatabase: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private var listener: ValueEventListener? = null

    private val _pendingApplications: MutableLiveData<MutableList<JobStatus>> =
        MutableLiveData(mutableListOf())
    val pendingApplications: LiveData<MutableList<JobStatus>> = _pendingApplications

    private val _evaluatedApplication: MutableLiveData<MutableList<JobStatus>> =
        MutableLiveData(mutableListOf())
    val evaluatedApplication: LiveData<MutableList<JobStatus>> = _evaluatedApplication


    fun fetchStudents(jobId: String) {
        val tempPendingList = mutableListOf<JobStatus>()
        val tempEvaluatedList = mutableListOf<JobStatus>()
        try {
            val companiesRef = mRealtimeDatabase.child(COLLECTION_PATH_COMPANY).child(jobId)
            listener = companiesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val applicationList = snapshot.children.map { application ->
                        application.getValue(JobApplication::class.java)!!
                    }
                    tempPendingList.clear()
                    tempEvaluatedList.clear()
                    viewModelScope.launch {
                        applicationList.forEach { applicant ->
                            val jobStatus = JobStatus()
                            val student = getStudent(applicant.studentId)
                            jobStatus.jobApplication = applicant
                            jobStatus.student = student
                            if (applicant.isEvaluated) {
                                tempEvaluatedList.add(jobStatus)
                            } else {
                                tempPendingList.add(jobStatus)
                            }
                        }
                        _pendingApplications.postValue(tempPendingList)
                        _evaluatedApplication.postValue(tempEvaluatedList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Error: ${error.message}")
                }

            })
        } catch (error: Exception) {
            Log.d(TAG, "Error: ${error.message}")
        } finally {
            tempPendingList.clear()
            tempEvaluatedList.clear()
            if (listener != null){
                mRealtimeDatabase.removeEventListener(listener!!)
                listener = null
                Log.d(TAG, "We are inside fetchStudent Function")
            }
        }
    }

    suspend fun getStudent(studentId: String): Student {
        val studentRef = mFirestore.collection("students")
        val student = studentRef.document(studentId)
            .get()
            .await()
            .toObject(Student::class.java)!!
        return student
    }

    fun setSelectionStatus(jobApplication: JobApplication) {
        viewModelScope.launch {
            val jobId = jobApplication.jobId
            val studentId = jobApplication.studentId
            val companiesRef = mRealtimeDatabase.child(COLLECTION_PATH_COMPANY).child(jobId).child(studentId)
            val companyName = mFirestore.collection(COLLECTION_PATH_COMPANY).document(jobId).get().await().get("name") as String
            if (jobApplication.applicationStatus == "Accepted"){
                val notification = HostNotification(
                    title = "Check Email",
                    body = "You have been selected for company $companyName",
                    hostId = jobApplication.studentId
                )
                mFirestore.collection(COLLECTION_PATH_NOTIFICATION).document(notification.id).set(notification).await()
            } else {
                val notification = HostNotification(
                    title = "Check Email",
                    body = "You aren't not selected for company $companyName",
                    hostId = jobApplication.studentId
                )
                mFirestore.collection(COLLECTION_PATH_NOTIFICATION).document(notification.id).set(notification).await()
            }

            companiesRef.setValue(jobApplication).await()
            Log.d(TAG, "Application Update Success")
        }
    }

    override fun onCleared() {
        if (listener != null) {
            Log.d(TAG, "Called and listener is removed")
            mRealtimeDatabase.removeEventListener(listener!!)
            listener = null
        }
        super.onCleared()
    }
}