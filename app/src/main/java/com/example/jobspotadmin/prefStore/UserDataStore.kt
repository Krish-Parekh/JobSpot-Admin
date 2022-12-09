package com.example.jobspotadmin.prefStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.jobspotadmin.util.Constants.PREFERENCES_KEY_FIVE
import com.example.jobspotadmin.util.Constants.PREFERENCES_KEY_FOUR
import com.example.jobspotadmin.util.Constants.PREFERENCES_KEY_ONE
import com.example.jobspotadmin.util.Constants.PREFERENCES_KEY_THREE
import com.example.jobspotadmin.util.Constants.PREFERENCES_KEY_TWO
import com.example.jobspotadmin.util.Constants.PREFERENCES_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class UserDataStore(context: Context) {
    private val dataStore = context.dataStore

    private object PreferencesKey {
        val isAuthenticated = booleanPreferencesKey(name = PREFERENCES_KEY_ONE)
        val isUserDetailFilled = booleanPreferencesKey(name = PREFERENCES_KEY_TWO)
        val roleType = stringPreferencesKey(name = PREFERENCES_KEY_THREE)
        val username = stringPreferencesKey(name = PREFERENCES_KEY_FOUR)
        val email = stringPreferencesKey(name = PREFERENCES_KEY_FIVE)
    }

    suspend fun setUserAuthStatus(isAuthenticated: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.isAuthenticated] = isAuthenticated
        }
    }

    suspend fun setUserDetailStatus(isUserDetailFilled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.isUserDetailFilled] = isUserDetailFilled
        }
    }

    suspend fun setRoleType(roleType: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.roleType] = roleType
        }
    }

    suspend fun setUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.username] = username
        }
    }

    suspend fun setEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.email] = email
        }
    }


    fun getUserAuthStatus(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val userAuthStatus = preferences[PreferencesKey.isAuthenticated] ?: false
                userAuthStatus
            }
    }

    fun getUserDetailStatus(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val userDetailStatus = preferences[PreferencesKey.isUserDetailFilled] ?: false
                userDetailStatus
            }
    }

    fun getRoleType(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val roleType = preferences[PreferencesKey.roleType] ?: ""
                roleType
            }
    }

    fun getUsername(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val roleType = preferences[PreferencesKey.username] ?: ""
                roleType
            }
    }

    fun getEmail(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val roleType = preferences[PreferencesKey.email] ?: ""
                roleType
            }
    }
}