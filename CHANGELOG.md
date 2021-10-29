# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

- dependency upgrade kotlin 1.4.20 to 1.5.30
- Bump spring-boot-dependencies from 2.4.2 to 2.5.6
- chages JwtConfigurationProperties from ```class``` to ```data class```

## [1.0.0]

### Added

- AutoConfigured ArgumentResolver for JWT identities
- ClaimSetTransformer to transform JWT claimSet
- RequestTokenResolver to resolve token from headers

[unreleased]: https://github.com/hndrs/jwt-auth-spring-boot-starter/compare/v1.0.0...HEAD

[1.0.0]: https://github.com/hndrs/jwt-auth-spring-boot-starter/compare/a9b56be382ab065e05c602815dba1d77536f6595...v1.0.0
