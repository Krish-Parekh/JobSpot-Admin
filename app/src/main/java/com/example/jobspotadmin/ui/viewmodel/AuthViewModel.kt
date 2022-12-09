package com.example.jobspotadmin.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobspotadmin.data.remote.FirebaseAuthRepository
import com.example.jobspotadmin.prefStore.UserDataStore
import com.example.jobspotadmin.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _uiState: MutableLiveData<UiState> = MutableLiveData()
    val uiState: LiveData<UiState> = _uiState

    var userAuthStatus: Boolean = false
    var userDetailStatus: Boolean = false
    var userRoleType: String = ""
    var username: String = ""
    var email: String = ""

    fun getUserAuthStatus() {
        viewModelScope.launch {
            userDataStore.getUserAuthStatus()
                .collect { isAuthenticated ->
                    userAuthStatus = isAuthenticated
                }
        }
    }

    fun getUserDetailStatus() {
        viewModelScope.launch {
            userDataStore.getUserDetailStatus()
                .collect { isUserDetailFilled ->
                    userDetailStatus = isUserDetailFilled
                }
        }
    }

    fun setRoleType(roleType: String) {
        viewModelScope.launch {
            userDataStore.setRoleType(roleType = roleType)

        }
    }

    fun getRoleType() {
        viewModelScope.launch {
            userDataStore.getRoleType()
                .collect { roleType ->
                    userRoleType = roleType
                }
        }
    }

    fun getUsername() {
        viewModelScope.launch {
            userDataStore.getUsername()
                .collect { username ->
                    this@AuthViewModel.username = username
                }
        }
    }

    fun getEmail() {
        viewModelScope.launch {
            userDataStore.getEmail()
                .collect { email ->
                    this@AuthViewModel.email = email
                }
        }
    }

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            firebaseAuthRepository.signUp(email, password)
                .collect { resultState ->
                    when (resultState) {
                        is ResultState.Success -> {
                            _uiState.postValue(
                                UiState(isAuthenticated = resultState.data)
                            )
                            userDataStore.setUserAuthStatus(isAuthenticated = true)
                            userDataStore.setUsername(username = username)
                            userDataStore.setEmail(email = email)
                        }
                        is ResultState.Failure -> {
                            _uiState.postValue(
                                UiState(
                                    error = resultState.error.message.toString()
                                )
                            )
                        }
                        ResultState.Loading -> {
                            _uiState.postValue(
                                UiState(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            firebaseAuthRepository.signIn(email, password)
                .collect { resultState ->
                    when (resultState) {
                        is ResultState.Success -> {
                            _uiState.postValue(
                                UiState(isAuthenticated = resultState.data)
                            )
                            userDataStore.setUserAuthStatus(isAuthenticated = true)
                        }
                        is ResultState.Failure -> {
                            _uiState.postValue(
                                UiState(
                                    error = resultState.error.message.toString()
                                )
                            )
                        }
                        ResultState.Loading -> {
                            _uiState.postValue(
                                UiState(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            firebaseAuthRepository.signOut()
                .collect { resultState ->
                    when (resultState) {
                        is ResultState.Success -> {
                            _uiState.postValue(
                                UiState(isAuthenticated = resultState.data)
                            )
                            userDataStore.setUserAuthStatus(isAuthenticated = false)
                        }
                        is ResultState.Failure -> {
                            _uiState.postValue(
                                UiState(
                                    error = resultState.error.message.toString()
                                )
                            )
                        }
                        ResultState.Loading -> {
                            _uiState.postValue(
                                UiState(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            firebaseAuthRepository.resetPassword(email)
                .collect { resultState ->
                    when (resultState) {
                        is ResultState.Success -> {
                            _uiState.postValue(
                                UiState(isAuthenticated = resultState.data)
                            )
                        }
                        is ResultState.Failure -> {
                            _uiState.postValue(
                                UiState(
                                    error = resultState.error.message.toString()
                                )
                            )
                        }
                        ResultState.Loading -> {
                            _uiState.postValue(
                                UiState(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
        }
    }
}


data class UiState(
    var isAuthenticated: Boolean = false,
    var isLoading: Boolean = false,
    var error: String = ""
)