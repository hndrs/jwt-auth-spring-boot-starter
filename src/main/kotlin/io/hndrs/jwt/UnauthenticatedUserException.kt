package io.hndrs.jwt

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UnauthenticatedUserException(msg: String? = null) :
    ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied: ${msg ?: "No message"}")
