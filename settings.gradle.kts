pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        id("io.spring.dependency-management").version("1.0.11.RELEASE")
        kotlin("jvm").version(kotlinVersion)
        kotlin("plugin.spring").version(kotlinVersion)
        kotlin("kapt").version(kotlinVersion)
        id("maven-publish")
        id("idea")
        id("io.hndrs.publishing-info").version("2.0.0")
        id("org.sonarqube").version("3.3")
    }
    repositories {
    }
}
