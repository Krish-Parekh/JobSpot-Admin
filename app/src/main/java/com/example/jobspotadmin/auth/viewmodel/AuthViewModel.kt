package com.example.jobspotadmin.auth.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "AuthViewModel"
class AuthViewModel : ViewModel() {
    private var imageUri : Uri? = null;
    private val mFireStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val mFireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun setImageUri(imageUri : Uri){
        this.imageUri = imageUri
    }

    fun getImageUri() : Uri? {
        return this.imageUri
    }

    fun uploadData(imageUri: Uri, user : User){
        val username = user.username
        val userId = user.uid
        val fileName = "$username:$userId"

        viewModelScope.launch(Dispatchers.IO){
            val imageDownloadUrl = withContext(Dispatchers.IO){
                uploadData(path = "tpo/image/${fileName}", fileUri = imageUri)
            }
            user.imageUri = imageDownloadUrl

            Log.d(TAG, "Image Uploaded Success")

            val uploadData = mFireStore
                .collection("tpo")
                .document(userId)
                .set(user)
                .await()

            Log.d(TAG, "Data Uploaded Success")
        }
    }

    private suspend fun uploadData(path: String, fileUri: Uri): String {
        val ref = mFireStorage.reference.child(path)
        ref.putFile(fileUri).asDeferred().await()

        val downloadUrl = ref.downloadUrl.asDeferred().await().toString()
        return downloadUrl
    }
}