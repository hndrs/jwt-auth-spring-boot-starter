package io.hndrs.jwt

/**
 * Interface to transform the claimSet into an object
 */
interface ClaimSetTransformer {

    fun transform(claimSet: Map<String, Any>): Any {
        return claimSet
    }
}
