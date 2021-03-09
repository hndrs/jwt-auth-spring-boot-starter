[![Maven Central](https://img.shields.io/maven-central/v/io.hndrs/hndrs_jwt-auth-spring-boot-starte?style=for-the-badge)](https://search.maven.org/artifact/io.hndrs/hndrs_jwt-auth-spring-boot-starte)
[![Coverage](https://img.shields.io/sonar/coverage/hndrs_jwt-auth-spring-boot-starte?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/dashboard?id=hndrs_jwt-auth-spring-boot-starte)
[![Supported Java Version](https://img.shields.io/badge/Supported%20Java%20Version-11%2B-informational?style=for-the-badge)]()

# Getting Started

Add the following dependency to the build file

```kotlin
dependencies {
    ...
    implementation("io.hndrs:jwt-auth-spring-boot-starter:1.0.0")
    ...
}
```

#### Configuration

Adding the issuer and the jwks path for the verification to the ```application.properties```

```properties
hndrs.jwt.issuer=https://domain.auth0.com
hndrs.jwt.key-store-path=/.well-known/jwks.json
```

#### Controller

```kotlin

@GetMapping("/user")
fun getUser(@User claimSet: Map<String, Any>): Map<String, Any> {
    // do something with the user claimSet
    return claimSet
}

```

#### RequestTokenResolver

By default the jwt token will be resolved from the ```Authorization``` Header in the following
format ```Bearer <jwt_token>```. To resolve the token from another header or in a different format a bean implementing
the [RequestTokenResolver](src/main/kotlin/io/hndrs/jwt/RequestTokenResolver.kt)
interface can be used.

```kotlin
    @Bean
fun requestTokenResolver(): RequestTokenResolver {
    return object : RequestTokenResolver {

        override fun tokenHeaderName(): String {
            return "x-custom-header"
        }

        override fun tokenResolver(header: String?): String {
            return header ?: throw UnauthenticatedUserException()
        }
    }
}

```

#### ClaimSetTransformer

By default the claimSet is represented as a ```Map<String, Any>``` to enrich or transform the map into a typed object a
bean implementing the [ClaimSetTransformer](src/main/kotlin/io/hndrs/jwt/ClaimSetTransformer.kt)
interface can be used.

```kotlin
data class CustomUser(val id: String, val name: String, val email: String)

@Bean
fun claimSetTransformer(): ClaimSetTransformer {
    return object : ClaimSetTransformer {
        override fun transform(claimSet: Map<String, Any>): Any {
            return CustomUser(
                claimSet["sub"] as String,
                claimSet["name"] as String,
                claimSet["email"] as String,
            )
        }
    }
}

// transformed object
@GetMapping("/user")
fun getUser(@User user: CustomUser): CustomUser {
    // do something with the user claimSet
    return claimSet
}
```

> Transforming claimSet to a CustomUser object

```kotlin
@Component
class UserLoadingClaimSetTransformer(
    private val userRepository: UserRepository
) : ClaimSetTransformer {
    override fun transform(claimSet: Map<String, Any>): Any {
        return userRepository.findById(claimSet["sub"] as String)
    }
}
```

> Loading a user object 