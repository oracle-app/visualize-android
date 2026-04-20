package com.oracle.visualize.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    Example
//    @Provides
//    @Singleton
//    fun providesAnyRepository(): AnyRepository {
//        return AnyRepository()
//    }

    @Provides
    @Singleton
    fun providesFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
}