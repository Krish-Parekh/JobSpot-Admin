package com.example.jobspotadmin.data.remote

import com.example.jobspotadmin.util.ResultState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
class EmailExists(message: String) : Exception(message)
class FirebaseAuthRepository constructor(
    private val firebaseAuth: FirebaseAuth
){
    // Sign up with email and password
    fun signUp(email: String, password: String): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySend(ResultState.Success(data = true))
            }
            .addOnFailureListener { exception ->
                if(exception is FirebaseAuthUserCollisionException){
                    val emailExistException = EmailExists(message = "Email already exists")
                    trySend(ResultState.Failure(error = emailExistException))
                }else {
                    trySend(ResultState.Failure(error = exception))
                }
            }
        awaitClose {
            close()
        }
    }

     fun signIn(email: String, password: String): Flow<ResultState<Boolean>> = callbackFlow {

         trySend(ResultState.Loading)
         firebaseAuth.signInWithEmailAndPassword(email, password)
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

    // Sign out
    fun signOut() : Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseAuth.signOut()
        trySend(ResultState.Success(data = true))

        awaitClose {
            close()
        }
    }

    fun resetPassword(email: String): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseAuth.sendPasswordResetEmail(email)
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