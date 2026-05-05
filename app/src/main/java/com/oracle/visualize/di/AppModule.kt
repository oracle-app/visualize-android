package com.oracle.visualize.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.oracle.visualize.data.datasources.AuthFirebaseSource
import com.oracle.visualize.data.datasources.VisualizationDataSource
import com.oracle.visualize.data.repositories.AuthRepositoryImpl
import com.oracle.visualize.data.repositories.TeamRepositoryImpl
import com.oracle.visualize.data.repositories.VisualizationRepositoryImpl
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.TeamRepository
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.data.repositories.UserRepositoryImpl
import com.oracle.visualize.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides Firebase-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provides a singleton instance of [FirebaseAuth].
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    /**
     * Provides a singleton instance of [FirebaseFirestore].
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    /**
     * Provides a singleton instance of [AuthFirebaseSource].
     *
     * @param auth The [FirebaseAuth] instance.
     */
    @Provides
    @Singleton
    fun provideAuthFirebaseSource(
        auth: FirebaseAuth
    ): AuthFirebaseSource = AuthFirebaseSource(auth)

    /**
     * Provides a singleton instance of [VisualizationDataSource].
     *
     * @param db The [FirebaseFirestore] instance.
     */
    @Provides
    @Singleton
    fun provideVisualizationDataSource(
        db: FirebaseFirestore
    ): VisualizationDataSource = VisualizationDataSource(db)

    /**
     * Alternative provider for [FirebaseFirestore] using the [Firebase] accessor.
     */
    fun providesFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    /**
     * Provides a singleton instance of [FirebaseStorage].
     */

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage =
        FirebaseStorage.getInstance()
}

/**
 * Hilt module that binds repository interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds [AuthRepositoryImpl] to [AuthRepository].
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    /**
     * Binds [VisualizationRepositoryImpl] to [VisualizationRepository].
     */
    @Binds
    @Singleton
    abstract fun bindVisualizationRepository(
        impl: VisualizationRepositoryImpl
    ): VisualizationRepository

    /**
     * Binds [TeamRepositoryImpl] to [TeamRepository].
     */
    @Binds
    @Singleton
    abstract fun bindTeamRepository(
        teamRepositoryImpl: TeamRepositoryImpl
    ): TeamRepository

    /**
     * Binds [UserRepositoryImpl] to [UserRepository].
     */
    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}
