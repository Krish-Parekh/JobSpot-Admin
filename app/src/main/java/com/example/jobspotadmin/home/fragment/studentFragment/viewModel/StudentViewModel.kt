package com.example.jobspotadmin.home.fragment.studentFragment.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.MockDetail
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
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
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
            mRealtimeDb.child(studentDatabasePath).removeValue()

            // remove companies from realtime db
            val companiesRef = mRealtimeDb.child(COLLECTION_PATH_COMPANY)
            val studentDeleteFromCompaniesDeffered = CompletableDeferred<Unit>()
            val studentCompanyDeleteListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { companyNode ->
                        val doStudentExist = companyNode.hasChild(studentId)
                        if (doStudentExist) {
                            companyNode.ref.child(studentId).removeValue()
                        }
                    }
                    studentDeleteFromCompaniesDeffered.complete(Unit)
                }

                override fun onCancelled(error: DatabaseError) {
                    studentDeleteFromCompaniesDeffered.completeExceptionally(error.toException())
                }
            }
            companiesRef.addValueEventListener(studentCompanyDeleteListener)
            studentDeleteFromCompaniesDeffered.invokeOnCompletion {
                Log.d(TAG, "Error : ${it?.message}")
                companiesRef.removeEventListener(studentCompanyDeleteListener)
            }
            studentDeleteFromCompaniesDeffered.await()

            // Remove from the mock test
            val mockTestRef = mRealtimeDb.child(COLLECTION_PATH_MOCK)
            val removeStudentIdDeffered = CompletableDeferred<Unit>()
            val studentMockTestDeleteListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (mockTestSnapshot in snapshot.children) {
                        val mockTestRef = mockTestSnapshot.ref
                        val studentIdsRef = mockTestRef.child("studentIds")

                        studentIdsRef.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(studentIdsSnapshot: DataSnapshot) {
                                val studentIds = studentIdsSnapshot.value as List<*>?
                                if (studentIds != null) {
                                    val updatedStudentIds = studentIds.filter { it != studentId }
                                    studentIdsRef.setValue(updatedStudentIds)
                                        .addOnSuccessListener {
                                            removeStudentIdDeffered.complete(Unit)
                                        }
                                        .addOnFailureListener{
                                            removeStudentIdDeffered.completeExceptionally(it)
                                        }

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                removeStudentIdDeffered.completeExceptionally(error.toException())
                            }

                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    removeStudentIdDeffered.completeExceptionally(error.toException())
                }
            }
            mockTestRef.addValueEventListener(studentMockTestDeleteListener)
            removeStudentIdDeffered.invokeOnCompletion {
                mockTestRef.removeEventListener(studentMockTestDeleteListener)
            }

            // remove student from mock result
            val mockResultRef = mRealtimeDb.child(COLLECTION_PATH_MOCK_RESULT)
            val studentDeleteFromMockResultDeffered = CompletableDeferred<Unit>()
            val studentMockResultDeleteListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { studentRef ->
                        val doStudentExist = studentRef.hasChild(studentId)
                        if (doStudentExist){
                            studentRef.child(studentId).ref.removeValue()
                        }
                    }
                    studentDeleteFromMockResultDeffered.complete(Unit)
                }
                override fun onCancelled(error: DatabaseError) {
                    studentDeleteFromMockResultDeffered.completeExceptionally(error.toException())
                }
            }
            mockResultRef.addValueEventListener(studentMockResultDeleteListener)
            studentDeleteFromMockResultDeffered.invokeOnCompletion {
                mockResultRef.removeEventListener(studentMockResultDeleteListener)
            }
            studentDeleteFromMockResultDeffered.await()
        }
    }
}