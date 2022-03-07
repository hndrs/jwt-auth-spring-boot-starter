import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

val kotlinVersion: String by extra

plugins {
    id("org.sonarqube")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
    id("maven-publish")
    id("idea")
    id("signing")
    jacoco
    id("io.hndrs.publishing-info")
}

val isRelease = project.hasProperty("release")

group = "io.hndrs"
version = "1.0.0".plus(if (isRelease) "" else "-SNAPSHOT")
java.sourceCompatibility = JavaVersion.VERSION_11


repositories {
    mavenCentral()
}

sonarqube {
    properties {
        property("sonar.projectKey", "hndrs_jwt-auth-spring-boot-starter")
        property("sonar.organization", "hndrs")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.exclusions", "**/sample/**")
    }
}

java {
    withJavadocJar()
}

configure<JacocoPluginExtension> {
    toolVersion = "0.8.7"
}

tasks.withType<JacocoReport> {
    reports {
        xml.apply {
            isEnabled = true
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    api(group = "org.springframework.boot", name = "spring-boot-autoconfigure")
    api(group = "org.springframework.boot", name = "spring-boot-starter-web")
    api(group = "com.nimbusds", name = "nimbus-jose-jwt", version = "9.21")

    annotationProcessor(group = "org.springframework.boot", name = "spring-boot-configuration-processor")
    kapt(group = "org.springframework.boot", name = "spring-boot-configuration-processor")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.12.2")
}

dependencyManagement {
    resolutionStrategy {
        cacheChangingModulesFor(0, "seconds")
    }
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:2.6.2") {
            bomProperty("kotlin.version", kotlinVersion)
        }
    }
}

val sourcesJarSubProject by tasks.creating(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    repositories {
        if (isRelease) {
            maven {
                name = "release"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                credentials {
                    username = System.getenv("SONATYPE_USER")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        } else {
            maven {
                name = "snapshot"
                url = uri("https://maven.pkg.github.com/hndrs/jwt-auth-spring-boot-starter")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
            artifact(sourcesJarSubProject)

            groupId = rootProject.group as? String
            artifactId = rootProject.name
            version = "${rootProject.version}${project.findProperty("version.appendix") ?: ""}"
        }
    }
    val signingKey: String? = System.getenv("SIGNING_KEY")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        signing {
            useInMemoryPgpKeys(groovy.json.StringEscapeUtils.unescapeJava(signingKey), signingPassword)
            sign(publications[project.name])
        }
    }
}

publishingInfo {
    name = rootProject.name
    description = "Simple JWT Auth User Library"
    url = "https://github.com/hndrs/jwt-auth-spring-boot-starter"
    license = io.hndrs.gradle.plugin.License(
        "https://github.com/hndrs/jwt-auth-spring-boot-starter/blob/main/LICENSE",
        "MIT License"
    )
    developers = listOf(
        io.hndrs.gradle.plugin.Developer("marvinschramm", "Marvin Schramm", "marvin.schramm@gmail.com")
    )
    organization = io.hndrs.gradle.plugin.Organization("hndrs", "https://oss.hndrs.io")
    scm = io.hndrs.gradle.plugin.Scm(
        "scm:git:git://github.com/hndrs/jwt-auth-spring-boot-starter",
        "https://github.com/hndrs/jwt-auth-spring-boot-starter"
    )
}

