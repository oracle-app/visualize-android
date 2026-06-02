package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.NotificationRepository
import com.oracle.visualize.domain.usecases.notification.MarkNotificationAsReadUseCase
import com.oracle.visualize.fixtures.NotificationFixtures
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MarkNotificationAsReadUCTest {
    @MockK
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var markNotificationAsReadUseCase: MarkNotificationAsReadUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        markNotificationAsReadUseCase = MarkNotificationAsReadUseCase(notificationRepository)
    }

    // Validation

    @Test
    fun blankNotificationID_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankNotificationID = ""

        // when
        val result = markNotificationAsReadUseCase(blankNotificationID)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) { notificationRepository.markAsRead(any()) }
    }

    // Repository Calls

    @Test
    fun validNotificationID_returnsResultSuccess_callsRepository() = runTest {
        // given
        coJustRun {
            notificationRepository.markAsRead(NotificationFixtures.VALID_NOTIFICATION_ID)
        }

        // when
        val result = markNotificationAsReadUseCase(NotificationFixtures.VALID_NOTIFICATION_ID)

        // then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            notificationRepository.markAsRead(NotificationFixtures.VALID_NOTIFICATION_ID)
        }
    }

    @Test
    fun repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = AppError.NetworkError("Failed to mark notification as read")
        coEvery { notificationRepository.markAsRead(any()) } throws exception

        // when
        val result = markNotificationAsReadUseCase(NotificationFixtures.VALID_NOTIFICATION_ID)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

}
