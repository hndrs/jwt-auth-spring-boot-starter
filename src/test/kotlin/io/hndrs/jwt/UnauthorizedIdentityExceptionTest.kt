package io.hndrs.jwt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class UnauthorizedIdentityExceptionTest {

    @Test
    fun nullMessage() {
        val ex = UnauthorizedIdentityException()
        Assertions.assertEquals("401 UNAUTHORIZED \"Access denied: No message\"", ex.message)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.status)
    }

    @Test
    fun providedMessage() {
        val expectedMessage = "Exception Reason message"
        val ex = UnauthorizedIdentityException(expectedMessage)
        Assertions.assertEquals("401 UNAUTHORIZED \"Access denied: $expectedMessage\"", ex.message)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.status)
    }
}
