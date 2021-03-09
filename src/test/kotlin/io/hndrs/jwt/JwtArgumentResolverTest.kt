package io.hndrs.jwt

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
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
    }

    private fun argumentResolver() = JwtArgumentResolver(mockk(), object : ClaimSetTransformer {}, object : RequestTokenResolver {})

    class TestClass {
        fun testMethod(@User annotatedParam: Map<String, Any>, unannotatedParam: Map<String, Any>) {}
    }
}
