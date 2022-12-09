package com.example.jobspotadmin.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.data.remote.FirebaseFirestoreRepository
import com.example.jobspotadmin.data.remote.FirebaseStorageRepository
import com.example.jobspotadmin.model.TpoDetail
import com.example.jobspotadmin.prefStore.UserDataStore
import com.example.jobspotadmin.util.ResultState
import com.google.firebase.firestore.DocumentId
import kotlinx.coroutines.launch
import javax.inject.Inject


data class UploadUiState(
    var isUploaded : Boolean = false,
    var isLoading : Boolean = false,
    var error : String = ""
)
class UserDetailViewModel @Inject constructor(
    private val firebaseFirestoreRepository: FirebaseFirestoreRepository,
    private val firebaseStorageRepository: FirebaseStorageRepository,
    private val dataStore: UserDataStore
) : ViewModel() {

    private val _uiState: MutableLiveData<UploadUiState> = MutableLiveData()
    val uiState: LiveData<UploadUiState> = _uiState

    private var imageUri : Uri? = null;

    fun setImageUri(imageUri : Uri){
        this.imageUri = imageUri
    }

    fun getImageUri() : Uri? {
        return this.imageUri
    }

    fun uploadData(
        imageUri : Uri,
        imagePath : String,
        collectionName : String,
        documentId: String,
        tpoDetail: TpoDetail
    ){
        viewModelScope.launch {
            firebaseStorageRepository
                .uploadFile<Uri>(file = imageUri, path = imagePath)
                .collect { resultState ->
                    when(resultState){
                        is ResultState.Success -> {
                            tpoDetail.imageUri = resultState.data
                            uploadToFirestore(collectionName, documentId, tpoDetail)
                        }
                        is ResultState.Failure -> {
                            _uiState.postValue(
                                UploadUiState(
                                    error = resultState.error.message.toString()
                                )
                            )
                        }
                        ResultState.Loading -> {
                            _uiState.postValue(
                                UploadUiState(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun uploadToFirestore(collectionName: String, documentId: String, tpoDetail: TpoDetail) {
        viewModelScope.launch {
            firebaseFirestoreRepository
                .saveDocument(collectionName = collectionName, documentId = documentId ,data = tpoDetail)
                .collect{ resultState ->
                    when(resultState){
                        is ResultState.Success -> {
                            _uiState.postValue(
                                UploadUiState(
                                    isUploaded = resultState.data
                                )
                            )
                            dataStore.setUserDetailStatus(isUserDetailFilled = true)
                        }
                        is ResultState.Failure -> {
                            _uiState.postValue(
                                UploadUiState(
                                    error = resultState.error.message.toString()
                                )
                            )
                        }
                        ResultState.Loading -> {
                            _uiState.postValue(
                                UploadUiState(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
        }
    }
}