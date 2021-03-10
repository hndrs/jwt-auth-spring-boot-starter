package io.hndrs.jwt

/**
 * Parameter marker annotation for the [JwtArgumentResolver]
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Identity
