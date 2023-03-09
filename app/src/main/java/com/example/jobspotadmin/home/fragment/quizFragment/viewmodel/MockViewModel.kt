package com.example.jobspotadmin.home.fragment.quizFragment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Mock
import com.example.jobspotadmin.model.MockDetail
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_MOCK
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_MOCK_RESULT
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.example.jobspotadmin.util.Resource
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "QuizViewModelTAG"

class MockViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mRealtimeDb: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private val mockTestRef = mRealtimeDb.child(COLLECTION_PATH_MOCK)
    private var mockListener: ValueEventListener? = null

    private val _mockTestUploadStatus: MutableLiveData<Resource<String>> = MutableLiveData()
    val mockTestUploadStatus: LiveData<Resource<String>> = _mockTestUploadStatus

    private val _mockDetails: MutableLiveData<List<MockDetail>> = MutableLiveData(mutableListOf())
    val mockDetails: LiveData<List<MockDetail>> = _mockDetails

    var mockQuestionCounter: Int = 1

    fun increment() {
        mockQuestionCounter += 1
    }

    fun decrement() {
        mockQuestionCounter -= 1
    }

    fun uploadMockTest(mock: Mock) {
        viewModelScope.launch {
            try {
                _mockTestUploadStatus.postValue(Resource.loading())
                val uid = mock.uid
                mFirestore.collection(COLLECTION_PATH_MOCK).document(uid).set(mock).await()
                val mockDetail = MockDetail(mockId = uid, mockName = mock.title)
                mRealtimeDb.child(COLLECTION_PATH_MOCK).child(uid).setValue(mockDetail)
                _mockTestUploadStatus.postValue(Resource.success("Mock upload success."))
            } catch (error: Exception) {
                val errorMessage = error.message!!
                _mockTestUploadStatus.postValue(Resource.error(errorMessage))
            }
        }
    }

    fun fetchMockTest() {
        mockListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mockDetailList = snapshot.children.map { quizDetail ->
                    quizDetail.getValue(MockDetail::class.java)!!
                }.sortedByDescending { it.mockId }
                _mockDetails.postValue(mockDetailList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error : ${error.message}")
            }
        }
        mockTestRef.addValueEventListener(mockListener!!)
    }

    fun deleteMockTest(mockDetail: MockDetail) {
        viewModelScope.launch {
            val mockId = mockDetail.mockId
            val mockTestDatabaseRef = "$COLLECTION_PATH_MOCK/$mockId"
            val mockTestResultDatabaseRef = "$COLLECTION_PATH_MOCK_RESULT/$mockId"
            // delete mock test from firestore
            mFirestore.collection(COLLECTION_PATH_MOCK).document(mockId).delete().await()

            // delete mock test from realtime db
            mRealtimeDb.child(mockTestDatabaseRef).removeValue().await()
            mRealtimeDb.child(mockTestResultDatabaseRef).removeValue().await()

            // mock test delete from all the students
            val studentRef = mRealtimeDb.child(COLLECTION_PATH_STUDENT)
            val mockTestDeleteDeffered = CompletableDeferred<Unit>()
            val mockTestDeleteListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { studentNode ->
                        val doesMockExist = studentNode.child(COLLECTION_PATH_MOCK).hasChild(mockId)
                        if (doesMockExist) {
                            studentNode.child(COLLECTION_PATH_MOCK).child(mockId).ref.removeValue()
                        }
                    }
                    mockTestDeleteDeffered.complete(Unit)
                }

                override fun onCancelled(error: DatabaseError) {
                    mockTestDeleteDeffered.completeExceptionally(error.toException())
                }
            }
            studentRef.addValueEventListener(mockTestDeleteListener)
            mockTestDeleteDeffered.invokeOnCompletion {
                studentRef.removeEventListener(mockTestDeleteListener)
            }
        }
    }

    override fun onCleared() {
        mockListener?.let {
            mockTestRef.removeEventListener(it)
        }
        super.onCleared()
    }
}