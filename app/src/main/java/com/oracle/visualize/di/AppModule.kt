package com.oracle.visualize.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.AuthFirebaseSource
import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.data.repositories.AuthRepositoryImpl
import com.oracle.visualize.data.repositories.VisualizationRepositoryImpl
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.VisualizationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    Example
//    @Provides
//    @Singleton
//    fun providesAnyRepository(): AnyRepository {
//        return AnyRepository()
//    }



}*/

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthFirebaseSource(
        auth: FirebaseAuth
    ): AuthFirebaseSource = AuthFirebaseSource(auth)

    @Provides
    @Singleton
    fun provideVisualizationDataSource(
        db: FirebaseFirestore
    ): VisualizationDataSource = VisualizationDataSource(db)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindVisualizationRepository(
        impl: VisualizationRepositoryImpl
    ): VisualizationRepository
}