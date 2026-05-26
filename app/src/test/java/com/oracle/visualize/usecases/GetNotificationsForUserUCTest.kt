package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.Notification
import com.oracle.visualize.domain.repositories.NotificationRepository
import com.oracle.visualize.domain.usecases.GetNotificationsForUserUseCase
import com.oracle.visualize.fixtures.NotificationFixtures
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetNotificationsForUserUCTest {

    @MockK
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var getNotificationsUseCase: GetNotificationsForUserUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getNotificationsUseCase = GetNotificationsForUserUseCase(notificationRepository)
    }

    // Validation

    @Test
    fun blankUserID_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankUserID = ""

        // when
        val result = getNotificationsUseCase(blankUserID)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.GeneralValidationError)
        coVerify(exactly = 0) { notificationRepository.getNotificationsForUser(any()) }
    }

    // Repository Calls

    @Test
    fun validUserID_returnsResultSuccess_withNotifications() = runTest {
        // given
        coEvery {
            notificationRepository.getNotificationsForUser(NotificationFixtures.VALID_USER_ID)
        } returns NotificationFixtures.fakeNotifications

        // when
        val result = getNotificationsUseCase(NotificationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(NotificationFixtures.fakeNotifications, result.getOrNull())
        coVerify(exactly = 1) {
            notificationRepository.getNotificationsForUser(NotificationFixtures.VALID_USER_ID)
        }
    }

    @Test
    fun userWithNoNotifications_returnsResultSuccess_withEmptyList() = runTest {
        // given
        coEvery {
            notificationRepository.getNotificationsForUser(NotificationFixtures.VALID_USER_ID)
        } returns emptyList()

        // when
        val result = getNotificationsUseCase(NotificationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isSuccess)
        assertEquals(emptyList<Notification>(), result.getOrNull())
    }

    @Test
    fun repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Network error")
        coEvery {
            notificationRepository.getNotificationsForUser(any())
        } throws exception

        // when
        val result = getNotificationsUseCase(NotificationFixtures.VALID_USER_ID)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
