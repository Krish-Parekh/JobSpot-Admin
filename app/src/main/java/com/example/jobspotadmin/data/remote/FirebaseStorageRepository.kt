package com.example.jobspotadmin.data.remote

import android.net.Uri
import com.example.jobspotadmin.util.ResultState
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseStorageRepository constructor(
    private val firebaseStorage: FirebaseStorage
) {

    fun <T> uploadFile(file: T, path: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val reference = firebaseStorage.reference.child(path)
        val uploadTask = reference.putFile(file as Uri).await()
        reference.downloadUrl
            .addOnSuccessListener { downloadUrl ->
                trySend(ResultState.Success(data = downloadUrl.toString()))
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Failure(error = exception))
            }

        awaitClose {
            close()
        }
    }
}