package io.hndrs.autoconfiguration

import com.nimbusds.jwt.proc.JWTProcessor
import io.hndrs.jwt.ClaimSetTransformer
import io.hndrs.jwt.JwtArgumentResolver
import io.hndrs.jwt.RequestTokenResolver
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

internal class JwtArgumentResolverAutoconfigurationTest {

    @Test
    fun addArgumentResolvers() {
        WebApplicationContextRunner()
            .withPropertyValues(
                "hndrs.jwt.issuer:https://issuer.domain.com",
                "hndrs.jwt.key-store-path=/.well-known/jwks.json"
            ).withConfiguration(AutoConfigurations.of(JwtArgumentResolverAutoconfiguration::class.java))
            .run {
                val bean = it.getBean(WebMvcConfigurer::class.java)
                val argumentResolvers = mutableListOf<HandlerMethodArgumentResolver>()
                bean.addArgumentResolvers(argumentResolvers)
                Assertions.assertTrue(argumentResolvers[0] is JwtArgumentResolver)
            }
    }

    @Test
    fun argumentResolver() {
        WebApplicationContextRunner()
            .withPropertyValues(
                "hndrs.jwt.issuer:https://issuer.domain.com",
                "hndrs.jwt.key-store-path=/.well-known/jwks.json"
            ).withConfiguration(AutoConfigurations.of(JwtArgumentResolverAutoconfiguration::class.java))
            .run {
                Assertions.assertDoesNotThrow { it.getBean(JwtArgumentResolver::class.java) }
            }
    }

    @Test
    fun claimSetTransformer() {
        WebApplicationContextRunner()
            .withPropertyValues(
                "hndrs.jwt.issuer:https://issuer.domain.com",
                "hndrs.jwt.key-store-path=/.well-known/jwks.json"
            ).withConfiguration(AutoConfigurations.of(JwtArgumentResolverAutoconfiguration::class.java))
            .run {
                Assertions.assertDoesNotThrow { it.getBean(ClaimSetTransformer::class.java) }
            }
    }

    @Test
    fun requestTokenResolver() {
        WebApplicationContextRunner()
            .withPropertyValues(
                "hndrs.jwt.issuer:https://issuer.domain.com",
                "hndrs.jwt.key-store-path=/.well-known/jwks.json"
            ).withConfiguration(AutoConfigurations.of(JwtArgumentResolverAutoconfiguration::class.java))
            .run {
                Assertions.assertDoesNotThrow { it.getBean(RequestTokenResolver::class.java) }
            }
    }

    @Test
    fun jwtProcessor() {
        WebApplicationContextRunner()
            .withPropertyValues(
                "hndrs.jwt.issuer:https://issuer.domain.com",
                "hndrs.jwt.key-store-path=/.well-known/jwks.json"
            ).withConfiguration(AutoConfigurations.of(JwtArgumentResolverAutoconfiguration::class.java))
            .run {
                Assertions.assertDoesNotThrow { it.getBean(JWTProcessor::class.java) }
            }
    }
}
