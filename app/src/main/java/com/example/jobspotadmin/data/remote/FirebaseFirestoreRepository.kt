package com.example.jobspotadmin.data.remote

import com.example.jobspotadmin.util.ResultState
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseFirestoreRepository constructor(
    private val firebaseStore: FirebaseFirestore
){
    fun <T : Any> saveDocument(collectionName : String, documentId: String, data : T) : Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseStore.collection(collectionName)
            .document(documentId)
            .set(data)
            .addOnSuccessListener {
                trySend(ResultState.Success(data = true))
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Failure(error = exception))
            }

        awaitClose {
            close()
        }
    }
}