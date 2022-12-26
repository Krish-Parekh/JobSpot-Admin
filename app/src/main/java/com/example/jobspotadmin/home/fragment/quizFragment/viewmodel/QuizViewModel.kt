package com.example.jobspotadmin.home.fragment.quizFragment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Quiz
import com.example.jobspotadmin.model.QuizDetail
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_QUIZ
import com.example.jobspotadmin.util.UiState
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "QuizViewModelTAG"

class QuizViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val mRealtimeDb: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    private val _quizUploadStatus: MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val quizUploadStatus: LiveData<UiState> = _quizUploadStatus

    private val _quizDetails: MutableLiveData<List<QuizDetail>> =
        MutableLiveData(mutableListOf())
    val quizDetails: LiveData<List<QuizDetail>> = _quizDetails

    var quizCounter: Int = 1

    fun increment() {
        quizCounter += 1
    }

    fun decrement() {
        quizCounter -= 1
    }

    fun uploadQuiz(quiz: Quiz) {
        viewModelScope.launch {
            try {
                _quizUploadStatus.postValue(UiState.LOADING)
                val uid = quiz.uid
                mFirestore.collection(COLLECTION_PATH_QUIZ).document(uid).set(quiz).await()
                val quizDetail = QuizDetail(quizId = uid, quizName = quiz.title)
                mRealtimeDb.child(COLLECTION_PATH_QUIZ).child(uid).setValue(quizDetail)
                _quizUploadStatus.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                Log.d(TAG, "${e.message}")
                _quizUploadStatus.postValue(UiState.FAILURE)
            }
        }
    }

    fun fetchQuiz() {
        mRealtimeDb.child(COLLECTION_PATH_QUIZ)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val quizDetailList = snapshot.children.map { quizDetail ->
                        quizDetail.getValue(QuizDetail::class.java)!!
                    }
                    _quizDetails.postValue(quizDetailList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Error : ${error.message}")
                }
            })
    }
}