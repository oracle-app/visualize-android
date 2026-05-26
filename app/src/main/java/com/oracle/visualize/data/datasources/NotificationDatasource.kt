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
    suspend fun getNotificationsForUser(userID: String): List<NotificationDTO> {
        val snapshot = notificationsRef.whereEqualTo("receiverID", userID).get().await()
        if (snapshot.isEmpty) return emptyList()

        return snapshot.documents.map { doc ->
            doc.toObject(NotificationDTO::class.java)
                ?: throw AppError.ParsingError("Failed to parse NotificationDTO: ${doc.id}")
        }
    }

    suspend fun markAllNotificationsAsRead(userID: String) {
        val snapshot = notificationsRef
            .whereEqualTo("receiverID", userID)
            .whereEqualTo("isRead",false)
            .get()
            .await()
        if (snapshot.isEmpty) return

        val batch = db.batch()
        snapshot.documents.forEach { doc ->
            batch.update(doc.reference, "isRead", true)
        }
        //Transaction
        batch.commit().await()
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        notificationsRef.document(notificationId)
            .update("isRead", true)
            .await()
    }


}
