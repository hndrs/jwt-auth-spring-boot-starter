package io.hndrs.jwt

import org.springframework.http.HttpHeaders

interface RequestTokenResolver {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    fun tokenHeaderName(): String = HttpHeaders.AUTHORIZATION

    fun tokenResolver(headerValue: String?): String {
        if (headerValue == null) {
            throw UnauthenticatedUserException("${tokenHeaderName()} Header not present")
        }
        if (!headerValue.startsWith(BEARER_PREFIX)) {
            throw UnauthenticatedUserException("Bearer Token is not present")
        }

        return headerValue.replace(BEARER_PREFIX, "")
    }
}
