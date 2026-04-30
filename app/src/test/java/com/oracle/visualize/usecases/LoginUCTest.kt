package com.oracle.visualize.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.AuthUser
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
    fun `blank email returns ValidationError and does not call repository`() = runTest {
        // given
        val blankEmail = ""
        val anyPassword = "123456"

        // when
        val result = loginUseCase(blankEmail, anyPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `invalid email format returns ValidationError and does not call repository`() = runTest {
        // given
        val invalidEmail = "notanemail"
        val anyPassword = "123456"

        // when
        val result = loginUseCase(invalidEmail, anyPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `email without TLD returns ValidationError and does not call repository`() = runTest {
        // given
        val emailWithoutTLD = "test@test"
        val anyPassword = "123456"

        // when
        val result = loginUseCase(emailWithoutTLD, anyPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }


    //Password Validation

    @Test
    fun `blank password returns ValidationError and does not call repository`() = runTest {
        // given
        val validEmail = "test@test.com"
        val blankPassword = ""

        // when
        val result = loginUseCase(validEmail, blankPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    //Repository

    @Test
    fun `valid credentials return Result success with AuthUser`() = runTest {
        // given
        val validEmail = "test@test.com"
        val validPassword = "123456"
        val fakeUser = AuthUser(uid = "123", email = validEmail)
        coEvery { authRepository.login(validEmail, validPassword) } returns fakeUser

        // when
        val result = loginUseCase(validEmail, validPassword)

        // then
        assertTrue(
            "Expected success but got: ${result.exceptionOrNull()?.message}",
            result.isSuccess
        )
        assertEquals(fakeUser, result.getOrNull())
        coVerify(exactly = 1) { authRepository.login(validEmail, validPassword) }
    }

    @Test
    fun `repository exception is caught and returned as Result failure`() = runTest {
        // given
        val validEmail = "test@test.com"
        val validPassword = "123456"
        val exception = Exception("Invalid credentials")
        coEvery { authRepository.login(any(), any()) } throws exception

        // when
        val result = loginUseCase(validEmail, validPassword)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }


}