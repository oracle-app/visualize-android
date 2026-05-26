package com.oracle.visualize.usecases

//class GetCurrentUserUCTest {
//    private class FakeAuthRepository(
//        private val currentUser: AuthUser?
//    ) : AuthRepository {
//
//        override suspend fun login(email: String, password: String): AuthUser {
//            throw NotImplementedError()
//        }
//
//        override suspend fun register(name: String, email: String, password: String): AuthUser {
//            throw NotImplementedError()
//        }
//
//        override fun logout() {}
//
//        override fun getCurrentUser(): AuthUser? {
//            return currentUser
//        }
//    }
//
//    @Test
//    fun `returns user when active session exists`() {
//        val fakeUser = AuthUser(
//            uid = "123",
//            email = "test@email.com"
//        )
//        val repository = FakeAuthRepository(fakeUser)
//        val useCase = GetCurrentUserUseCase(repository)
//        val result = useCase()
//        assertEquals(fakeUser, result)
//    }
//
//    @Test
//    fun `returns null when active session does not exist`() {
//        val repository = FakeAuthRepository(null)
//        val useCase = GetCurrentUserUseCase(repository)
//        val result = useCase()
//        assertNull(result)
//    }
//}
