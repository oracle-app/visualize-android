package com.oracle.visualize.data.datasources

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthFirebaseSource @Inject constructor(private val auth: FirebaseAuth) {
    suspend fun login(email: String, password: String): FirebaseUser {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user ?: throw AppError.AuthFailed("Login failed: User object is null")
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.AuthFailed(e.message ?: "Authentication failed during login")
        }
    }

    suspend fun register(email: String, password: String): FirebaseUser {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user ?: throw AppError.AuthFailed("Registration failed: User object is null")
        } catch (e: Exception) {
            if (e is AppError) throw e
            throw AppError.AuthFailed(e.message ?: "Authentication failed during registration")
        }
    }

    fun logout() = auth.signOut()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}