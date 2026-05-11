package com.oracle.visualize.data.repositories

import com.oracle.visualize.R
import com.oracle.visualize.domain.models.Notification
import com.oracle.visualize.domain.repositories.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [NotificationRepository] providing mock notification data.
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {

    /**
     * Retrieves a flow of mock notifications with varied dates.
     * Currently provides exactly 7 notifications for testing consistency.
     * 
     * @return A [Flow] containing a list of [Notification] objects.
     */
    override fun getNotifications(): Flow<List<Notification>> {
        val calendar = Calendar.getInstance()
        val now = Date()
        
        // Today - 5 mins ago
        calendar.time = now
        calendar.add(Calendar.MINUTE, -5)
        val todayMinus5 = calendar.time

        // Today - 2 hours ago
        calendar.time = now
        calendar.add(Calendar.HOUR_OF_DAY, -2)
        val todayMinus2h = calendar.time
        
        // Yesterday - 1 day and 3 hours ago
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        calendar.add(Calendar.HOUR_OF_DAY, -3)
        val yesterday = calendar.time
        
        // Yesterday - 1 day and 10 hours ago
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        calendar.add(Calendar.HOUR_OF_DAY, -10)
        val yesterdayNight = calendar.time

        // Last 30 days - 3 days ago
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, -3)
        val threeDaysAgo = calendar.time

        // Last 30 days - 10 days ago
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, -10)
        val tenDaysAgo = calendar.time

        // Last 30 days - 25 days ago
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, -25)
        val twentyFiveDaysAgo = calendar.time

        return flowOf(
            listOf(
                Notification("1", "Oracle System", "Welcome to Visualize! Start creating your first team.", todayMinus5, R.drawable.profile_placeholder),
                Notification("2", "John Doe", "John Doe mentioned you in a comment on 'Project Alpha'.", todayMinus2h, R.drawable.profile_placeholder),
                Notification("3", "Sarah Smith", "Sarah shared a new report 'Q1 Analysis' with you.", yesterday, R.drawable.profile_placeholder),
                Notification("4", "Teams Bot", "New members have joined your 'Data Science' team.", yesterdayNight, R.drawable.profile_placeholder),
                Notification("5", "Lucy Martinez", "Lucy Martinez shared a new chart called 'Sales by Region'.", threeDaysAgo, R.drawable.profile_placeholder),
                Notification("6", "Market Update", "New market trends are available for review.", tenDaysAgo, R.drawable.profile_placeholder),
                Notification("7", "System Maintenance", "Scheduled maintenance completed successfully.", twentyFiveDaysAgo, R.drawable.profile_placeholder)
            )
        )
    }
}
