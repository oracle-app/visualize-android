package com.oracle.visualize.usecases

import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.RegisterUseCase
import com.oracle.visualize.fixtures.UserFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.oracle.visualize.domain.exceptions.AppError
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK


/**
 * Unit tests for [RegisterUseCase].
 *
 *   RegisterUseCase validates email and password before delegating to
 *   [AuthRepository]. It wraps results in [Result] instead of throwing,
 *   so we assert on Result.isSuccess / Result.isFailure and the specific
 *   error type rather than catching exceptions.

 */

class RegisterUseCaseTest {

    @MockK
    private lateinit var authRepository: AuthRepository
    private lateinit var registerUseCase: RegisterUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        registerUseCase = RegisterUseCase(authRepository)
    }


    //Email Validation

    @Test
    fun blankEmail_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankEmail = ""

        // when
        val result = registerUseCase(blankEmail, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun invalidEmailFormat_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val invalidEmail = "notanemail"

        // when
        val result = registerUseCase(invalidEmail, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun emailWithoutTLD_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val emailWithoutTLD = "test@test"

        // when
        val result = registerUseCase(emailWithoutTLD, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    //Password Validation

    @Test
    fun blankPassword_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val blankPassword = ""

        // when
        val result = registerUseCase(UserFixtures.VALID_EMAIL, blankPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun passwordShorterThan6Chars_returnsValidationError_doesNotCallRepository() = runTest {
        // given
        val shortPassword = "12345" // 5 chars — boundary value just below minimum

        // when
        val result = registerUseCase(UserFixtures.VALID_EMAIL, shortPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun passwordOfExactly6Chars_passesValidation_callsRepository() = runTest {
        // given - boundary value: exactly at the minimum
        coEvery { authRepository.register(any(), any()) } returns UserFixtures.fakeAuthUser

        // when
        val result = registerUseCase(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            authRepository.register(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)
        }
    }

    @Test
    fun passwordOver6Chars_passesValidation_callsRepository() = runTest {
        // given - boundary value: safely above minimum
        val longPassword = "12345678900"
        coEvery { authRepository.register(any(), any()) } returns UserFixtures.fakeAuthUser

        // when
        val result = registerUseCase(UserFixtures.VALID_EMAIL, longPassword)

        // then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { authRepository.register(UserFixtures.VALID_EMAIL, longPassword) }
    }

    //Repository

    @Test
    fun validCredentials_returnsResultSuccess_withAuthUser() = runTest {
        // given
        coEvery {
            authRepository.register(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)
        } returns UserFixtures.fakeAuthUser

        // when
        val result = registerUseCase(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isSuccess)
        assertEquals(UserFixtures.fakeAuthUser, result.getOrNull())
        coVerify(exactly = 1) {
            authRepository.register(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)
        }
    }

    @Test
    fun repositoryThrows_returnsResultFailure() = runTest {
        // given
        val exception = Exception("Email already in use")
        coEvery { authRepository.register(any(), any()) } throws exception

        // when
        val result = registerUseCase(UserFixtures.VALID_EMAIL, UserFixtures.VALID_PASSWORD)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
