package com.oracle.visualize.usecases

import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.RegisterUseCase
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
    fun `blank email returns ValidationError and does not call repository`() = runTest {
        // given
        val blankEmail = ""
        val anyPassword = "123456"

        // when
        val result = registerUseCase(blankEmail, anyPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun `invalid email format returns ValidationError and does not call repository`() = runTest {
        // given
        val invalidEmail = "notanemail"
        val anyPassword = "123456"

        // when
        val result = registerUseCase(invalidEmail, anyPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun `email without TLD returns ValidationError and does not call repository`() = runTest {
        // given
        val emailWithoutTLD = "test@test"
        val anyPassword = "123456"

        // when
        val result = registerUseCase(emailWithoutTLD, anyPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }


    //Password Validation

    @Test
    fun `blank password returns ValidationError and does not call repository`() = runTest {
        // given
        val validEmail = "test@test.com"
        val blankPassword = ""

        // when
        val result = registerUseCase(validEmail, blankPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun `password shorter than 6 characters returns ValidationError and does not call repository`() = runTest {
        // given
        val validEmail = "test@test.com"
        val shortPassword = "12345" // 5 chars — boundary value just below minimum

        // when
        val result = registerUseCase(validEmail, shortPassword)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.ValidationError)
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun `password of exactly 6 characters passes validation and calls repository`() = runTest {
        // given
        val validEmail = "test@test.com"
        val boundaryPassword = "123456" // exactly 6 chars — boundary value at minimum
        val fakeUser = AuthUser(uid = "123", email = validEmail)
        coEvery { authRepository.register(any(), any()) } returns fakeUser

        // when
        val result = registerUseCase(validEmail, boundaryPassword)

        // then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { authRepository.register(validEmail, boundaryPassword) }
    }

    @Test
    fun `password over 6 characters passes validation and calls repository`() = runTest {
        // given
        val validEmail = "test@test.com"
        val boundaryPassword = "12345689400"
        val fakeUser = AuthUser(uid = "123", email = validEmail)
        coEvery { authRepository.register(any(), any()) } returns fakeUser

        // when
        val result = registerUseCase(validEmail, boundaryPassword)

        // then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { authRepository.register(validEmail, boundaryPassword) }
    }



    //Repository

    @Test
    fun `valid credentials return Result success with AuthUser`() = runTest {
        // given
        val validEmail = "test@test.com"
        val validPassword = "123456"
        val fakeUser = AuthUser(uid = "123", email = validEmail)
        coEvery { authRepository.register(validEmail, validPassword) } returns fakeUser

        // when
        val result = registerUseCase(validEmail, validPassword)

        // then
        assertTrue(result.isSuccess)
        assertEquals(fakeUser, result.getOrNull())
        coVerify(exactly = 1) { authRepository.register(validEmail, validPassword) }
    }

    @Test
    fun `repository exception is caught and returned as Result failure`() = runTest {
        // given
        val validEmail = "test@test.com"
        val validPassword = "123456"
        val exception = Exception("Email already in use")
        coEvery { authRepository.register(any(), any()) } throws exception

        // when
        val result = registerUseCase(validEmail, validPassword)

        // then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}