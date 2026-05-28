package com.oracle.visualize.data.datasources

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Data source for Firebase Authentication.
 *
 * @property auth The [FirebaseAuth] instance used for authentication operations.
 */
class AuthFirebasesource @Inject constructor(private val auth: FirebaseAuth) {
    /**
     * Attempts to log in a user with the provided email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return The [FirebaseUser] if login is successful.
     * @throws AppError.AuthFailed If login fails or the user object is null.
     */
    suspend fun login(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw AppError.AuthFailed("Login failed: User object is null")
    }

    /**
     * Attempts to register a new user with the provided email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return The [FirebaseUser] if registration is successful.
     * @throws AppError.AuthFailed If registration fails or the user object is null.
     */
    suspend fun register(email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user ?: throw AppError.AuthFailed("Registration failed: User object is null")
    }

    /**
     * Logs out the currently authenticated user.
     */
    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    /**
     * Gets the currently authenticated [FirebaseUser], if any.
     *
     * @return The current [FirebaseUser] or null if no user is logged in.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}
