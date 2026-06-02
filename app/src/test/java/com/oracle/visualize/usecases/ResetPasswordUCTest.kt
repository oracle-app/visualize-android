package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.auth.ResetPasswordUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.oracle.visualize.fixtures.UserFixtures

/**
 * Unit tests for [ResetPasswordUseCase].
 *
 * ResetPasswordUseCase validates the email format before delegating to
 * [AuthRepository]. It wraps results in [Result], so we assert on
 * Result.isSuccess / Result.isFailure.
 */
class ResetPasswordUCTest {

    @MockK
    private lateinit var authRepository: AuthRepository
    private lateinit var resetPasswordUseCase: ResetPasswordUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        resetPasswordUseCase = ResetPasswordUseCase(authRepository)
    }

    // Email Validation

    @Test
    fun blankEmail_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankEmail = ""

        // when
        val result = resetPasswordUseCase(blankEmail)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.AuthValidationError)
        coVerify(exactly = 0) { authRepository.resetPassword(any()) }
    }

    @Test
    fun invalidEmailFormat_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val invalidEmail = "notanemail"

        // when
        val result = resetPasswordUseCase(invalidEmail)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.AuthValidationError)
        coVerify(exactly = 0) { authRepository.resetPassword(any()) }
    }

    @Test
    fun emailWithoutTLD_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val emailWithoutTLD = "test@test"

        // when
        val result = resetPasswordUseCase(emailWithoutTLD)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.AuthValidationError)
        coVerify(exactly = 0) { authRepository.resetPassword(any()) }
    }

    // Repository Calls

    @Test
    fun validEmail_returnsResultSuccess_withUnit() = runTest {
        // given
        // Assuming your repository returns a Result<Unit> on success
        coEvery {
            authRepository.resetPassword(UserFixtures.VALID_EMAIL)
        } returns Result.success(Unit)

        // when
        val result = resetPasswordUseCase(UserFixtures.VALID_EMAIL)

        // then
        assertTrue(
            "Expected success but got: ${result.exceptionOrNull()?.message}",
            result.isSuccess
        )
        assertEquals(Unit, result.getOrNull())
        coVerify(exactly = 1) {
            authRepository.resetPassword(UserFixtures.VALID_EMAIL)
        }
    }

    @Test
    fun invalidEmailOrNetworkError_returnsResultFailure_throwsFailure() = runTest {
        // given
        val exception = AppError.NetworkError("No internet connection")

        coEvery {
            authRepository.resetPassword(any())
        } throws exception

        // when
        val result = resetPasswordUseCase(UserFixtures.VALID_EMAIL)

        // then
        // 2. We add messages to the asserts. This way, if they fail, you'll know exactly why.
        assertTrue(
            "TEST FAILED: Expected a Failure, but the UseCase returned Success.",
            result.isFailure
        )

        val obtainedError = result.exceptionOrNull()
        assertTrue(
            "TEST FAILED: Expected a NetworkError, but received: ${obtainedError?.javaClass?.simpleName}",
            obtainedError is AppError.NetworkError
        )

        coVerify(exactly = 1) {
            authRepository.resetPassword(UserFixtures.VALID_EMAIL)
        }


    }
}

