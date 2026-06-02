package com.oracle.visualize.domain.usecases.notification


import com.oracle.visualize.domain.models.enums.NotificationGroup
import com.oracle.visualize.domain.models.Notification
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class GroupNotificationsUseCase @Inject constructor(){

    operator fun invoke(notifications: List<Notification>)
    :Map<NotificationGroup, List<Notification>>
    {
        fun Date.toCalendarDate(): Triple<Int, Int, Int> {
            val cal = Calendar.getInstance()
            cal.time = this
            return Triple(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        }

        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val thirtyDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }

        val todayTriple = Triple(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
        val yesterdayTriple = Triple(yesterday.get(Calendar.YEAR), yesterday.get(Calendar.MONTH), yesterday.get(Calendar.DAY_OF_MONTH))

        return notifications.groupBy { notification ->
            val notifDate = notification.createdAt.toCalendarDate()
            val notifCal = Calendar.getInstance().apply { time = notification.createdAt }

            when{
                notifDate == todayTriple          -> NotificationGroup.TODAY
                notifDate == yesterdayTriple      -> NotificationGroup.YESTERDAY
                notifCal >= thirtyDaysAgo  -> NotificationGroup.LAST30
                else                        -> NotificationGroup.OLDER
            }
        }


    }
}
