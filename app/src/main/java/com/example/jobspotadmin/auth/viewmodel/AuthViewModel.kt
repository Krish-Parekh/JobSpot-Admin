package com.example.jobspotadmin.auth.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.User
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_TPO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UiState(
    var loading : Boolean = false,
    var success : Boolean = false,
    val failed : Boolean = false,
)

private const val TAG = "AuthViewModel"
class AuthViewModel : ViewModel() {
    private var imageUri : Uri? = null;
    private val mFireStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val mFireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _uploadDataStatus : MutableLiveData<UiState> = MutableLiveData(UiState())
    val uploadDataStatus : LiveData<UiState> = _uploadDataStatus

    fun setImageUri(imageUri : Uri){
        this.imageUri = imageUri
    }

    fun getImageUri() : Uri? {
        return this.imageUri
    }

    fun uploadData(imageUri: Uri, user : User){
        viewModelScope.launch {
            try {
                _uploadDataStatus.postValue(
                    UiState(loading = true)
                )
                // ImageUpload
                val userId = user.uid
                val imageName = user.uid
                val storageRef = mFireStorage.reference.child("tpo/profileImage/${imageName}")
                storageRef.putFile(imageUri).await()
                val imageUrl = storageRef.downloadUrl.await().toString()
                user.imageUri = imageUrl
                Log.d(TAG, "Image Uploaded Success")

                // UserData Upload
                mFireStore.collection(COLLECTION_PATH_TPO).document(userId).set(user).await()
                Log.d(TAG, "Data Uploaded Success")

                _uploadDataStatus.postValue(
                    UiState(success = true)
                )
            }catch (error : Exception){
                Log.d(TAG, "Exception : ${error.message}")
                _uploadDataStatus.postValue(
                    UiState(
                        failed = true
                    )
                )
            }
        }
    }
}