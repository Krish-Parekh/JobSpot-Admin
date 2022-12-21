package com.example.jobspotadmin.home.fragment.jobsFragment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.JobApplication
import com.example.jobspotadmin.model.JobStatus
import com.example.jobspotadmin.model.Student
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "StudentJobViewModelTAG"

class StudentJobViewModel : ViewModel() {
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mRealtimeDatabase: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    private val _pendingApplications: MutableLiveData<MutableList<JobStatus>> = MutableLiveData(mutableListOf())
    val pendingApplications: LiveData<MutableList<JobStatus>> = _pendingApplications

    private val _evaluatedApplication: MutableLiveData<MutableList<JobStatus>> = MutableLiveData(mutableListOf())
    val evaluatedApplication: LiveData<MutableList<JobStatus>> = _evaluatedApplication

    fun fetchStudents(jobId: String) {
        val tempPendingList = mutableListOf<JobStatus>()
        val tempEvaluatedList = mutableListOf<JobStatus>()
        try {
            val companiesRef = mRealtimeDatabase.child(COLLECTION_PATH_COMPANY).child(jobId)
            companiesRef.addValueEventListener(object : ValueEventListener {
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
                        Log.d(TAG, "Evaluated Students = ${tempEvaluatedList}")
                        Log.d(TAG, "Pending Students = ${tempPendingList}")
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
        }
    }

    suspend fun getStudent(studentId: String): Student {
        val studentRef = mFirestore.collection("students")
        return withContext(Dispatchers.Default) {
            studentRef.document(studentId)
                .get()
                .await()
                .toObject(Student::class.java)!!
        }
    }

    fun setSelectionStatus(jobApplication: JobApplication) {
        viewModelScope.launch {
            val jobId = jobApplication.jobId
            val studentId = jobApplication.studentId
            val companiesRef = mRealtimeDatabase.child(COLLECTION_PATH_COMPANY).child(jobId).child(studentId)
            companiesRef.setValue(jobApplication).await()
            Log.d(TAG, "Application Update Success")
        }
    }
}