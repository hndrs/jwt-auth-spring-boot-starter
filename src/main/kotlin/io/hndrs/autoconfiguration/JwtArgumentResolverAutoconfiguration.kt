package io.hndrs.autoconfiguration

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.nimbusds.jwt.proc.JWTProcessor
import io.hndrs.autoconfiguration.JwtConfigurationProperties.Companion.PROPERTY_PREFIX
import io.hndrs.jwt.ClaimSetTransformer
import io.hndrs.jwt.JwtArgumentResolver
import io.hndrs.jwt.RequestTokenResolver
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.net.URL

@ConditionalOnWebApplication
@EnableConfigurationProperties(JwtConfigurationProperties::class)
@Configuration
class JwtArgumentResolverAutoconfiguration(
    private val properties: JwtConfigurationProperties
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(argumentResolver())
    }

    /**
     * [JwtArgumentResolver] that allows injecting jwt claims with [io.hndrs.jwt.Identity]
     */
    @Bean
    open fun argumentResolver(): JwtArgumentResolver {
        return JwtArgumentResolver(jwtProcessor(), claimSetTransformer(), requestTokenResolver())
    }


    /**
     * Anonymous default implementation of [ClaimSetTransformer]
     * that is [ConditionalOnMissingBean]
     */
    @Bean
    @ConditionalOnMissingBean
    fun claimSetTransformer(): ClaimSetTransformer {
        return object : ClaimSetTransformer {}
    }

    /**
     * Anonymous default implementation of [RequestTokenResolver]
     * that is [ConditionalOnMissingBean]
     */
    @Bean
    @ConditionalOnMissingBean
    fun requestTokenResolver(): RequestTokenResolver {
        return object : RequestTokenResolver {}
    }

    /**
     * [JWTProcessor] that parses and verifies the jwt token
     */
    @Bean
    open fun jwtProcessor(): JWTProcessor<SecurityContext> {
        val keySelector = JWSVerificationKeySelector(
            JWSAlgorithm.RS256,
            RemoteJWKSet(URL(properties.keyStorePath))
        )
        val jwtProcessor = DefaultJWTProcessor<SecurityContext>()
        jwtProcessor.jwsKeySelector = keySelector
        return jwtProcessor
    }
}

@ConfigurationProperties(PROPERTY_PREFIX)
@ConstructorBinding
data class JwtConfigurationProperties(
    /**
     * Keystore Path that needs to point to a valid jwks
     */
    val keyStorePath: String
) {

    companion object {
        const val PROPERTY_PREFIX = "hndrs.jwt"
    }
}
