package com.oracle.visualize.di

import com.oracle.visualize.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.auth.FirebaseAuth
import com.oracle.visualize.data.datasources.AnalyzeApiMicroService
import com.oracle.visualize.data.datasources.AuthFirebasesource
import com.oracle.visualize.data.datasources.TeamDatasource
import com.oracle.visualize.data.datasources.VisualizationDatasource
import com.oracle.visualize.data.repositories.AnalyzeRepositoryImpl
import com.oracle.visualize.data.repositories.AuthRepositoryImpl
import com.oracle.visualize.data.repositories.CommentRepositoryImpl
import com.oracle.visualize.data.repositories.TeamRepositoryImpl
import com.oracle.visualize.data.repositories.VisualizationRepositoryImpl
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.TeamRepository
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.data.repositories.UserRepositoryImpl
import com.oracle.visualize.domain.repositories.AnalyzeRepository
import com.oracle.visualize.domain.repositories.CommentRepository
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.domain.repositories.NotificationRepository
import com.oracle.visualize.data.repositories.NotificationRepositoryImpl


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
     * Provides a singleton instance of [AuthFirebasesource].
     *
     * @param auth The [FirebaseAuth] instance.
     */
    @Provides
    @Singleton
    fun provideAuthFirebaseSource(
        auth: FirebaseAuth
    ): AuthFirebasesource = AuthFirebasesource(auth)

    /**
     * Provides a singleton instance of [VisualizationDatasource].
     *
     * @param db The [FirebaseFirestore] instance.
     */
    @Provides
    @Singleton
    fun provideVisualizationDataSource(
        db: FirebaseFirestore
    ): VisualizationDatasource = VisualizationDatasource(db)

    /**
     * Alternative provider for [FirebaseFirestore] using the [Firebase] accessor.
     */
    fun providesFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
    @Provides
    @Singleton
    fun provideAnalyzeRepository(
        apiMicroService: AnalyzeApiMicroService
    ) : AnalyzeRepository {
        return AnalyzeRepositoryImpl(apiMicroService)
    }
}

/**
 * Hilt module that provides Network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MICROSERVICES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides a singleton instance of [AnalyzeApiMicroService].
     */
    @Provides
    @Singleton
    fun provideAnalyzeApiMicroService(retrofit: Retrofit): AnalyzeApiMicroService {
        return retrofit.create(AnalyzeApiMicroService::class.java)
    }
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

    /**
     * Binds [CommentRepositoryImpl] to [CommentRepository].
     */
    @Binds
    abstract fun bindCommentRepository(
        impl: CommentRepositoryImpl
    ): CommentRepository

    /**
     * Binds [NotificationRepositoryImpl] to [NotificationRepository].
     */
    @Binds
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository
}
