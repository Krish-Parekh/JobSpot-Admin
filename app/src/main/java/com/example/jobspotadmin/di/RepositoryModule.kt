package com.example.jobspotadmin.di

import android.content.Context
import com.example.jobspotadmin.data.remote.FirebaseAuthRepository
import com.example.jobspotadmin.data.remote.FirebaseFirestoreRepository
import com.example.jobspotadmin.prefStore.UserDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserDataStore(
        @ApplicationContext context : Context
    ) : UserDataStore {
        return UserDataStore(context = context)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthRepository(firebaseAuth: FirebaseAuth) : FirebaseAuthRepository {
        return FirebaseAuthRepository(firebaseAuth = firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideFirebaseFireStoreRepository(firebaseFirestore: FirebaseFirestore) : FirebaseFirestoreRepository {
        return FirebaseFirestoreRepository(firebaseStore = firebaseFirestore)
    }
}