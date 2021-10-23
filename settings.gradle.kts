pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        id("io.spring.dependency-management").version("1.0.11.RELEASE")
        kotlin("jvm").version(kotlinVersion)
        kotlin("plugin.spring").version(kotlinVersion)
        kotlin("kapt").version(kotlinVersion)
        id("maven-publish")
        id("idea")
    }
    repositories {
    }
}
