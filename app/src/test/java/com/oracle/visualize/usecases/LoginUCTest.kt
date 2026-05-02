package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.LoginUseCase
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
 * Unit tests for [LoginUseCase].
 *
 *   LoginUseCase validates email and password before delegating to
 *   [AuthRepository]. It wraps results in [Result] instead of throwing,
 *   so we assert on Result.isSuccess / Result.isFailure and the specific
 *   error type rather than catching exceptions.

 */

class LoginUCTest {

    @MockK
    private lateinit var authRepository: AuthRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        loginUseCase = LoginUseCase(authRepository)
    }

    //Email Validation

    @Test
    fun blankEmail_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankEmail = ""

        // when
        val result = loginUseCase(blankEmail, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun invalidEmailFormat_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val invalidEmail = "notanemail"

        // when
        val result = loginUseCase(invalidEmail, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun emailWithoutTLD_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val emailWithoutTLD = "test@test"

        // when
        val result = loginUseCase(emailWithoutTLD, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }


    //Password Validation

    @Test
    fun blankPassword_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankPassword = ""

        // when
        val result = loginUseCase(UserFixtures.VALID_EMAIL, blankPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    //Repository Calls

    @Test
    fun validCredentials_returnsResultSuccess_withAuthUser() = runTest {
        // given
        coEvery {
            authRepository.login(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)
        } returns UserFixtures.fakeAuthUser

        // when
        val result = loginUseCase(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(
            "Expected success but got: ${result.exceptionOrNull()?.message}",
            result.isSuccess
        )
        assertEquals(UserFixtures.fakeAuthUser, result.getOrNull())
        coVerify(exactly = 1) {
            authRepository.login(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)
        }
    }

    @Test
    fun invalidCredentials_returnsResultFailure_throwsFailure() = runTest {
        // given
        val exception = Exception("Invalid credentials")
        coEvery { authRepository.login(any(), any()) } throws exception

        // when
        val result = loginUseCase(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}