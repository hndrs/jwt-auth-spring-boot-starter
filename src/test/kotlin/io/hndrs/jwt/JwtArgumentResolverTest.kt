package io.hndrs.jwt

import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.proc.BadJWTException
import com.nimbusds.jwt.proc.JWTProcessor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.web.context.request.NativeWebRequest
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.javaMethod

internal class JwtArgumentResolverTest {

    @Test
    fun supportsParameter() {
        val supportedParameterFunction = TestClass::class.declaredMemberFunctions.first()
        val methodParameter = MethodParameter(supportedParameterFunction.javaMethod!!, 0)
        assertTrue(argumentResolver().supportsParameter(methodParameter))
    }

    @Test
    fun doesNotSupportsParameter() {
        val supportedParameterFunction = TestClass::class.declaredMemberFunctions.first()
        val methodParameter = MethodParameter(supportedParameterFunction.javaMethod!!, 1)

        assertFalse(argumentResolver().supportsParameter(methodParameter))
    }

    @Test
    fun resolveArgument() {
        val jwtProcessor = mockk<JWTProcessor<SecurityContext>>()
        val argumentResolver = JwtArgumentResolver(jwtProcessor, object : ClaimSetTransformer {}, object : RequestTokenResolver {})

        val supportedParameterFunction = TestClass::class.declaredMemberFunctions.first()
        val methodParameter = MethodParameter(supportedParameterFunction.javaMethod!!, 0)


        //native webRequest returns null
        assertThrows(UnauthorizedIdentityException::class.java) {
            argumentResolver.resolveArgument(methodParameter, null, mockk() {
                every { getNativeRequest(HttpServletRequest::class.java) } returns null
            }, null)
        }

        //token resolver returns null
        assertThrows(UnauthorizedIdentityException::class.java) {
            argumentResolver.resolveArgument(methodParameter, null, mockk() {
                every { getNativeRequest(HttpServletRequest::class.java) } returns mockk() {
                    every { getHeader(any()) } returns null
                }
            }, null)
        }


        val nativeWebRequestWithToken = mockk<NativeWebRequest>() {
            every { getNativeRequest(HttpServletRequest::class.java) } returns mockk() {
                every { getHeader(any()) } returns "Bearer $TEST_TOKEN"
            }
        }

        //BadJWTException with Message
        every { jwtProcessor.process(any<JWT>(), null) } throws BadJWTException("Message")

        val badJWTExceptionWithMessage = assertThrows(UnauthorizedIdentityException::class.java) {
            argumentResolver.resolveArgument(methodParameter, null, nativeWebRequestWithToken, null)
        }
        assertEquals("401 UNAUTHORIZED \"Access denied: BadJWTException (Message)\"", badJWTExceptionWithMessage.message)
        clearMocks(jwtProcessor)

        //BadJWTException with Message
        every { jwtProcessor.process(any<JWT>(), null) } throws BadJWTException(null)

        val badJWTExceptionWithoutMessage = assertThrows(UnauthorizedIdentityException::class.java) {
            argumentResolver.resolveArgument(methodParameter, null, nativeWebRequestWithToken, null)
        }
        assertEquals("401 UNAUTHORIZED \"Access denied: BadJWTException (No message)\"", badJWTExceptionWithoutMessage.message)
        clearMocks(jwtProcessor)


        //BadJOSEException with Message
        every { jwtProcessor.process(any<JWT>(), null) } throws BadJOSEException("Message")

        val badJOSEExceptionWithMessage = assertThrows(UnauthorizedIdentityException::class.java) {
            argumentResolver.resolveArgument(methodParameter, null, nativeWebRequestWithToken, null)
        }
        clearMocks(jwtProcessor)
        assertEquals("401 UNAUTHORIZED \"Access denied: BadJOSEException (Message)\"", badJOSEExceptionWithMessage.message)

        //BadJOSEException with Message
        every { jwtProcessor.process(any<JWT>(), null) } throws BadJOSEException(null)

        val badJOSEExceptionWithoutMessage = assertThrows(UnauthorizedIdentityException::class.java) {
            argumentResolver.resolveArgument(methodParameter, null, nativeWebRequestWithToken, null)
        }
        clearMocks(jwtProcessor)
        assertEquals("401 UNAUTHORIZED \"Access denied: BadJOSEException (No message)\"", badJOSEExceptionWithoutMessage.message)

        //Any other exception with Message
        every { jwtProcessor.process(any<JWT>(), null) } throws IllegalStateException("Message")

        val anyExceptionWithMessage = assertThrows(UnauthorizedIdentityException::class.java) {
            argumentResolver.resolveArgument(methodParameter, null, nativeWebRequestWithToken, null)
        }
        clearMocks(jwtProcessor)
        assertEquals("401 UNAUTHORIZED \"Access denied: IllegalStateException (Message)\"", anyExceptionWithMessage.message)

        //Any other exception without Message
        every { jwtProcessor.process(any<JWT>(), null) } throws IllegalStateException()

        val anyExceptionWithoutMessage = assertThrows(UnauthorizedIdentityException::class.java) {
            argumentResolver.resolveArgument(methodParameter, null, nativeWebRequestWithToken, null)
        }
        clearMocks(jwtProcessor)
        assertEquals("401 UNAUTHORIZED \"Access denied: IllegalStateException (No message)\"", anyExceptionWithoutMessage.message)

        every { jwtProcessor.process(any<JWT>(), null) } returns mockk() {
            every { claims } returns mapOf(
                "sub" to "1234567890"
            )
        }

        val resolvedArgument = argumentResolver.resolveArgument(methodParameter, null, nativeWebRequestWithToken, null)

        assertEquals(
            mapOf(
                "sub" to "1234567890"
            ), resolvedArgument
        )
    }

    private fun argumentResolver() = JwtArgumentResolver(mockk(), object : ClaimSetTransformer {}, object : RequestTokenResolver {})

    class TestClass {
        fun testMethod(@Identity annotatedParam: Map<String, Any>, unannotatedParam: Map<String, Any>) {}
    }

    companion object {
        private const val TEST_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
    }
}
