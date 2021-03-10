package io.hndrs.jwt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class UnauthenticatedIdentityExceptionTest {

    @Test
    fun nullMessage() {
        val ex = UnauthenticatedUserException()
        Assertions.assertEquals("401 UNAUTHORIZED \"Access denied: No message\"", ex.message)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.status)
    }

    @Test
    fun providedMessage() {
        val expectedMessage = "Exception Reason message"
        val ex = UnauthenticatedUserException(expectedMessage)
        Assertions.assertEquals("401 UNAUTHORIZED \"Access denied: $expectedMessage\"", ex.message)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.status)
    }
}
