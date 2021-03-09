package io.hndrs.jwt

import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.proc.BadJWTException
import com.nimbusds.jwt.proc.JWTProcessor
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import javax.servlet.http.HttpServletRequest


class JwtArgumentResolver(
    private val jwtProcessor: JWTProcessor<SecurityContext>,
    private val claimSetTransformer: ClaimSetTransformer,
    private val requestTokenResolver: RequestTokenResolver,
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) = parameter.hasParameterAnnotation(User::class.java)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)

        val resolvedToken = requestTokenResolver.tokenResolver(
            request?.getHeader(requestTokenResolver.tokenHeaderName())
        )

        val resolvedClaimSet = resolveClaimSet(resolvedToken)
        return claimSetTransformer.transform(resolvedClaimSet)
    }

    /**
     * Extract authentication details from token
     */
    @Throws(Exception::class)
    private fun resolveClaimSet(token: String): Map<String, Any> {

        try {
            val jwt: JWT = JWTParser.parse(token)
            val claimsSet: JWTClaimsSet = jwtProcessor.process(jwt, null)
            return claimsSet.claims

        } catch (e: BadJWTException) {
            throw UnauthenticatedUserException("${e.javaClass.simpleName} (${e.message ?: NO_MESSAGE})")
        } catch (e: BadJOSEException) {
            throw UnauthenticatedUserException("${e.javaClass.simpleName} (${e.message ?: NO_MESSAGE})")
        } catch (e: Exception) {
            throw UnauthenticatedUserException("${e.javaClass.simpleName} (${e.message ?: NO_MESSAGE})")
        }
    }

    companion object {
        private const val NO_MESSAGE = "No message"
    }

}



