package com.example.jobspotadmin.home.fragment.quizFragment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Mock
import com.example.jobspotadmin.model.MockDetail
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_MOCK
import com.example.jobspotadmin.util.UiState
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "QuizViewModelTAG"

class MockViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mRealtimeDb: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    private val _mockTestUploadStatus: MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val mockTestUploadStatus: LiveData<UiState> = _mockTestUploadStatus

    private val _mockDetails: MutableLiveData<List<MockDetail>> =
        MutableLiveData(mutableListOf())
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
                _mockTestUploadStatus.postValue(UiState.LOADING)
                val uid = mock.uid
                mFirestore.collection(COLLECTION_PATH_MOCK).document(uid).set(mock).await()
                val mockDetail = MockDetail(quizId = uid, quizName = mock.title)
                mRealtimeDb.child(COLLECTION_PATH_MOCK).child(uid).setValue(mockDetail)
                _mockTestUploadStatus.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                Log.d(TAG, "${e.message}")
                _mockTestUploadStatus.postValue(UiState.FAILURE)
            }
        }
    }

    fun fetchMockTest() {
        mRealtimeDb.child(COLLECTION_PATH_MOCK)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mockDetailList = snapshot.children.map { quizDetail ->
                        quizDetail.getValue(MockDetail::class.java)!!
                    }
                    _mockDetails.postValue(mockDetailList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Error : ${error.message}")
                }
            })
    }
}