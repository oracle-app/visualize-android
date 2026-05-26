package com.oracle.visualize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.oracle.visualize.data.datasources.NotificationDatasource
import com.oracle.visualize.data.repositories.NotificationRepositoryImpl
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.util.Log
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

@RunWith(AndroidJUnit4::class)
class NotificationIntegrationTest {

    private lateinit var datasource: NotificationDatasource
    private lateinit var repository: NotificationRepositoryImpl

    // TODO: Replace with a real userID from your Firestore
    private val testUserID = "aB26cJBIA4NErpnXDLSw0s1ossJ2"
    private var testNotificationID: String = ""

    @Before
    fun setUp() {
        val db = FirebaseFirestore.getInstance()
        datasource = NotificationDatasource(db)
        repository = NotificationRepositoryImpl(datasource)

        runBlocking {
            val snapshot = db.collection("notifications")
                .whereEqualTo("receiverID", testUserID)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val batch = db.batch()
                snapshot.documents.forEach { doc ->
                    batch.update(doc.reference, "isRead", false)
                }
                batch.commit().await()
            }

            val notifications = repository.getNotificationsForUser(testUserID)
            testNotificationID = notifications.firstOrNull()?.id ?: ""
            Log.d("NotificationTest", "Reset complete, using notificationID: $testNotificationID")
        }
    }

    @Test
    fun fetchNotificationsForUser_logsResults() = runBlocking {
        val notifications = repository.getNotificationsForUser(testUserID)

        Log.d("NotificationTest", "Fetch All: Fetched ${notifications.size} notifications")
        notifications.forEach {
            Log.d("NotificationTest", it.toString())
        }
        assertNotNull(notifications)
    }

    @Test
    fun markAllNotificationsAsRead_logsResult() = runBlocking {
        repository.markAllAsRead(testUserID)
        Log.d("NotificationTest", "Successfully marked all notifications as read for $testUserID")

        val notifications = repository.getNotificationsForUser(testUserID)
        Log.d("NotificationTest", "Notifications after markAllAsRead:")
        notifications.forEach {
            Log.d("NotificationTest", "id: ${it.id} | isRead: ${it.isRead} | body: ${it.body}")
        }
    }

    @Test
    fun markNotificationAsRead_logsResult() = runBlocking {
        repository.markAsRead(testNotificationID)
        Log.d("NotificationTest", "Successfully marked notification $testNotificationID as read")

        val notifications = repository.getNotificationsForUser(testUserID)
        Log.d("NotificationTest", "Notifications after markAsRead:")
        notifications.forEach {
            Log.d("NotificationTest", "id: ${it.id} | isRead: ${it.isRead} | body: ${it.body}")
        }
    }
}
