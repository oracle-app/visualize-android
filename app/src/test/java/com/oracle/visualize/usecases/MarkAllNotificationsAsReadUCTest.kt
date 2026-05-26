package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.NotificationRepository
import com.oracle.visualize.domain.usecases.MarkAllNotificationsAsReadUseCase
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

class MarkAllNotificationsAsReadUCTest {
    @MockK
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        markAllNotificationsAsReadUseCase = MarkAllNotificationsAsReadUseCase(notificationRepository)
    }

    // Validation

    @Test
    fun blankUserID_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankUserID = ""

        // when
        val result = markAllNotificationsAsReadUseCase(blankUserID)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) { notificationRepository.markAllAsRead(any()) }
    }

    // Repository Calls

    @Test
    fun validUserID_returnsResultSuccess_callsRepository() = runTest {
        // given
        coJustRun {
            notificationRepository.markAllAsRead(NotificationFixtures.VALID_USER_ID)
        }

        // when
        val result = markAllNotificationsAsReadUseCase(NotificationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            notificationRepository.markAllAsRead(NotificationFixtures.VALID_USER_ID)
        }
    }

    @Test
    fun repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = AppError.NetworkError("Failed to mark all notifications as read")
        coEvery { notificationRepository.markAllAsRead(any()) } throws exception

        // when
        val result = markAllNotificationsAsReadUseCase(NotificationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

}
