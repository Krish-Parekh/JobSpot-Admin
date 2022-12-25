package com.example.jobspotadmin.home.fragment.quizFragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.Quiz
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_QUIZ
import com.example.jobspotadmin.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class QuizViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _quizUploadStatus: MutableLiveData<UiState> = MutableLiveData(UiState.LOADING)
    val quizUploadStatus: LiveData<UiState> = _quizUploadStatus

    fun uploadQuiz(quiz: Quiz) {
        viewModelScope.launch {
            try {
                _quizUploadStatus.postValue(UiState.LOADING)
                val uid = quiz.uid
                mFirestore.collection(COLLECTION_PATH_QUIZ).document(uid).set(quiz).await()
                _quizUploadStatus.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                _quizUploadStatus.postValue(UiState.FAILURE)
            }
        }
    }

}