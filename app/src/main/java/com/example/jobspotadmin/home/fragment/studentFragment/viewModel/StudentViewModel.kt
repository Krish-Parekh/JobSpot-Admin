package com.example.jobspotadmin.home.fragment.studentFragment.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Student
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_COMPANY
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_MOCK
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_MOCK_RESULT
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_ROLE
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.example.jobspotadmin.util.Constants.Companion.PROFILE_IMAGE_PATH
import com.example.jobspotadmin.util.Constants.Companion.RESUME_PATH
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "StudentViewModelTAG"

class StudentViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mRealtimeDb: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private val mStorage: StorageReference by lazy { FirebaseStorage.getInstance().reference }

    private val _students: MutableLiveData<List<Student>> = MutableLiveData(mutableListOf())
    val students: LiveData<List<Student>> = _students

    fun fetchStudents() {
        viewModelScope.launch {
            mFirestore.collection(COLLECTION_PATH_STUDENT)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    val documents = value?.documents!!
                    val studentList = documents.map {
                        it.toObject(Student::class.java)!!
                    }
                    _students.postValue(studentList)
                }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            val studentId = student.uid!!
            val studentImagePath = "$PROFILE_IMAGE_PATH/$studentId"
            val studentResumePath = "$RESUME_PATH/$studentId"
            val studentDatabasePath = "$COLLECTION_PATH_STUDENT/$studentId"

            // remove student images
            mStorage.child(studentImagePath).delete().await()

            // remove student resume
            mStorage.child(studentResumePath).delete().await()

            // remove from student collection
            mFirestore.collection(COLLECTION_PATH_STUDENT).document(studentId).delete().await()

            // remove student roles
            mFirestore.collection(COLLECTION_PATH_ROLE).document(studentId).delete().await()

            // remove students from realtime db
            mRealtimeDb.child(studentDatabasePath).removeValue().await()

            // remove student from companies in realtime db
            deleteStudentFromCompany(studentId)

            // remove student from mock test in realtimeDb
            deleteStudentFromMockTest(studentId)

            // remove student from mock result in realtimeDb
            deleteStudentFromMockResult(studentId)
        }
    }

    private suspend fun deleteStudentFromCompany(studentId: String) {
        val companiesRef = mRealtimeDb.child(COLLECTION_PATH_COMPANY)
        val studentDeleteFromCompaniesDeffered = CompletableDeferred<Unit>()
        val studentCompanyDeleteListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { companyNode ->
                    val doStudentExist = companyNode.hasChild(studentId)
                    if (doStudentExist){
                        companyNode.ref.child(studentId).removeValue()
                    }
                }
                studentDeleteFromCompaniesDeffered.complete(Unit)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error : ${error.message}")
                studentDeleteFromCompaniesDeffered.completeExceptionally(error.toException())
            }
        }
        companiesRef.addValueEventListener(studentCompanyDeleteListener)
        studentDeleteFromCompaniesDeffered.invokeOnCompletion {
            Log.d(TAG, "deleteStudentFromCompany: ${it?.message}")
            companiesRef.removeEventListener(studentCompanyDeleteListener)
        }
        studentDeleteFromCompaniesDeffered.await()
    }
    private suspend fun deleteStudentFromMockTest(studentId: String) {
        val mockTestRef = mRealtimeDb.child(COLLECTION_PATH_MOCK)
        val studentMockTestDeffered = CompletableDeferred<Unit>()
        val studentMockTestDeleteListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { mockTestNode ->
                    val mockTestNodeRef = mockTestNode.ref
                    val studentIdsRef = mockTestNodeRef.child("studentIds")
                    studentIdsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val studentIds = snapshot.value as List<*>?
                            if (studentIds != null){
                                val updateStudentIds = studentIds.filter { it != studentId }
                                snapshot.ref.setValue(updateStudentIds)
                                    .addOnSuccessListener {
                                        studentMockTestDeffered.complete(Unit)
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d(TAG, "deleteStudentFromMockTest: ${exception.message}")
                                        studentMockTestDeffered.completeExceptionally(exception)
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d(TAG, "deleteStudentFromMockTest: ${error.message}")
                            studentMockTestDeffered.completeExceptionally(error.toException())
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "deleteStudentFromMockTest: ${error.message}")
                studentMockTestDeffered.completeExceptionally(error.toException())
            }
        }
        mockTestRef.addValueEventListener(studentMockTestDeleteListener)
        studentMockTestDeffered.invokeOnCompletion {
            Log.d(TAG, "deleteStudentFromMockTest: ${it?.message}")
            mockTestRef.removeEventListener(studentMockTestDeleteListener)
        }
        studentMockTestDeffered.await()
    }
    private suspend fun deleteStudentFromMockResult(studentId: String) {
        val mockResultRef = mRealtimeDb.child(COLLECTION_PATH_MOCK_RESULT)
        val studentDeleteFromMockResultDeffered = CompletableDeferred<Unit>()
        val studentMockResultDeleteListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { mockResultNode ->
                    val doStudentExist = mockResultNode.hasChild(studentId)
                    if (doStudentExist){
                        mockResultNode.child(studentId).ref.removeValue()
                    }
                }
                studentDeleteFromMockResultDeffered.complete(Unit)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "deleteStudentFromMockResult: ${error.message}")
                studentDeleteFromMockResultDeffered.completeExceptionally(error.toException())
            }
        }
        mockResultRef.addValueEventListener(studentMockResultDeleteListener)
        studentDeleteFromMockResultDeffered.invokeOnCompletion {
            Log.d(TAG, "deleteStudentFromMockResult: ${it?.message}")
            mockResultRef.removeEventListener(studentMockResultDeleteListener)
        }
        studentDeleteFromMockResultDeffered.await()
    }
}