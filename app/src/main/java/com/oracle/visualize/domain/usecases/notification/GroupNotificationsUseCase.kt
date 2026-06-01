package com.oracle.visualize.domain.usecases.notification

import android.os.Build
import androidx.annotation.RequiresApi
import com.oracle.visualize.domain.models.enums.NotificationGroup
import com.oracle.visualize.domain.models.Notification
import com.oracle.visualize.domain.repositories.NotificationRepository
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GroupNotificationsUseCase @Inject constructor(){

    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(notifications: List<Notification>)
    :Map<NotificationGroup, List<Notification>>
    {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val thirtyDaysAgo = today.minusDays(30)

        return notifications.groupBy { notification ->
            val notifDate = notification.createdAt
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            when{
                notifDate == today          -> NotificationGroup.TODAY
                notifDate == yesterday      -> NotificationGroup.YESTERDAY
                notifDate >= thirtyDaysAgo  -> NotificationGroup.LAST30
                else                        -> NotificationGroup.OLDER
            }
        }


    }
}
