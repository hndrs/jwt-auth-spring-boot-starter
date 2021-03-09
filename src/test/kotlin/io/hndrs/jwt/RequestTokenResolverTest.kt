package io.hndrs.jwt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders

internal class RequestTokenResolverTest {

    @Test
    fun tokenHeaderName() {
        val requestTokenResolver = object : RequestTokenResolver {}
        Assertions.assertEquals(HttpHeaders.AUTHORIZATION, requestTokenResolver.tokenHeaderName())
    }

    @Test
    fun tokenResolver() {
        val testToken = "TestToken"
        val requestTokenResolver = object : RequestTokenResolver {}

        val actual = requestTokenResolver.tokenResolver("Bearer $testToken")
        Assertions.assertEquals(testToken, actual)

        Assertions.assertThrows(UnauthenticatedUserException::class.java) {
            requestTokenResolver.tokenResolver(null)
        }

        Assertions.assertThrows(UnauthenticatedUserException::class.java) {
            requestTokenResolver.tokenResolver("TokenWithoutBearer Prefix")
        }
    }
}
