package com.oracle.visualize.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.AuthFirebaseSource
import com.oracle.visualize.data.repositories.AuthRepositoryImpl
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.LoginUseCase
import com.oracle.visualize.domain.usecases.LogoutUseCase
import com.oracle.visualize.domain.usecases.RegisterUseCase
import com.oracle.visualize.presentation.screens.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    // DataSource
    single { AuthFirebaseSource(get()) }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // Use Cases
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }

    // viewModel
    viewModel { LoginViewModel(get()) }
}