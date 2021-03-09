package io.hndrs.jwt

/**
 * Interface to transform the claimset into an object
 */
interface ClaimSetTransformer {

    fun transform(claimSet: Map<String, Any>): Any {
        return claimSet
    }
}
