pluginManagement {
    apply(from = "dependencies.gradle.kts")

    val kotlinVersion: String by extra
    val springDependencyManagement: String by extra
    val publishingInfo: String by extra
    val sonarVersion: String by extra

    plugins {
        id("io.spring.dependency-management").version(springDependencyManagement)
        kotlin("jvm").version(kotlinVersion)
        kotlin("plugin.spring").version(kotlinVersion)
        kotlin("kapt").version(kotlinVersion)
        id("maven-publish")
        id("idea")
        id("io.hndrs.publishing-info").version(publishingInfo)
        id("org.sonarqube").version(sonarVersion)
    }
    repositories {
    }
}
