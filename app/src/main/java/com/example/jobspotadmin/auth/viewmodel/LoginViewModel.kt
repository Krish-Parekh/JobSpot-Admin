package com.example.jobspotadmin.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_ROLE
import com.example.jobspotadmin.util.Constants.Companion.COLLECTION_PATH_TPO
import com.example.jobspotadmin.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginUiState(
    val username : String = "",
    val email : String = "",
    val userInfoExist : Boolean = false,
    val roleType : String = ""
)

class LoginViewModel : ViewModel() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _loginStatus: MutableLiveData<Resource<LoginUiState>> = MutableLiveData()
    val loginStatus: LiveData<Resource<LoginUiState>> = _loginStatus

    fun authenticateUser(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                _loginStatus.postValue(Resource.loading())
                mAuth.signInWithEmailAndPassword(email, password).await()
                val currentUserId = mAuth.currentUser?.uid!!
                val currentUsername = mAuth.currentUser?.displayName!!

                val currentUserRoleRef = mFirestore.collection(COLLECTION_PATH_ROLE).document(currentUserId)
                val currentUserRole = currentUserRoleRef.get().await()
                if (!currentUserRole.exists()){
                    _loginStatus.postValue(Resource.error("Invalid Credentials"))
                    return@launch
                }
                val roleType : String = currentUserRole.get("role") as String
                val currentUserRef = mFirestore.collection(COLLECTION_PATH_TPO).document(currentUserId)
                val currentUser = currentUserRef.get().await()
                val currentUserInfoExist = currentUser.exists()
                val loginUiState = LoginUiState(
                    username = currentUsername,
                    email = email,
                    roleType = roleType,
                    userInfoExist = currentUserInfoExist
                )
                _loginStatus.postValue(Resource.success(loginUiState))
            } catch (e: Exception) {
                _loginStatus.postValue(Resource.error(e.message.toString()))
            }
        }
    }
}