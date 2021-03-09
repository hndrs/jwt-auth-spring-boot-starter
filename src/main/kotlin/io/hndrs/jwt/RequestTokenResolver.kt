package io.hndrs.jwt

import org.springframework.http.HttpHeaders

interface RequestTokenResolver {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    fun tokenHeaderName(): String = HttpHeaders.AUTHORIZATION

    fun tokenResolver(header: String?): String {
        if (header == null) {
            throw UnauthenticatedUserException("${tokenHeaderName()} Header not present")
        }
        if (!header.startsWith(BEARER_PREFIX)) {
            throw UnauthenticatedUserException("Bearer Token is not present")
        }

        return header.replace(BEARER_PREFIX, "")
    }
}
