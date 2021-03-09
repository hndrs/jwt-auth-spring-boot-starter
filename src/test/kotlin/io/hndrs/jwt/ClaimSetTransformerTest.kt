package io.hndrs.jwt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ClaimSetTransformerTest {

    @Test
    fun transform() {
        val claimSetTransformer = object : ClaimSetTransformer {}

        val claimSet = mapOf<String, Any>(
            "key1" to "value1",
            "key2" to "value2",
            "key3" to "value3",
        )

        val transform = claimSetTransformer.transform(claimSet)

        Assertions.assertEquals(claimSet, transform)
    }
}
