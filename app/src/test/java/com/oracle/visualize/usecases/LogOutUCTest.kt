package com.oracle.visualize.usecases

import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.LogoutUseCase
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.verify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


/**
 * Unit tests for [LogoutUseCase].
 *
 *   LogoutUseCase just clears the cache of the token, without a network call.
 *   Does not need a coroutine.

 */

class LogOutUCTest {

    @MockK
    private lateinit var authRepository: AuthRepository
    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        logoutUseCase = LogoutUseCase(authRepository)
    }

    @Test
    fun `calls repository once`() {
        // No values given
        every { authRepository.logout() } just Runs

        // when
        logoutUseCase()

        // then
        verify(exactly = 1) { authRepository.logout() }
    }
}