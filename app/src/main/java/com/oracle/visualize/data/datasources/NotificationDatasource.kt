package com.oracle.visualize.data.datasources

import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.domain.exceptions.AppError
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.oracle.visualize.data.datasources.dtos.NotificationDTO

/**
 * Data source for notification-related operations using Firestore.
 *
 * @property db The [FirebaseFirestore] instance used for database operations.
 */
class NotificationDatasource @Inject constructor(
    private val db: FirebaseFirestore,
){

    private val notificationsRef = db.collection("notifications")

    /**
     * Fetches all notifications sent to a specific user.
     *
     * @param userID The unique ID of the user.
     * @return A list of [NotificationDTO] objects sent to the user.
     * @throws AppError.NetworkError If a network error occurs.
     */
    suspend fun getNotificationsUserHas(userID: String): List<NotificationDTO> {
        val snapshot = notificationsRef.whereEqualTo("userID", userID).get().await()
        if (snapshot.isEmpty) return emptyList()

        return snapshot.toObjects(NotificationDTO::class.java)
    }



}
