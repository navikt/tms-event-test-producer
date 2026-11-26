plugins {
    alias(managed.plugins.kotlin)
    alias(managed.plugins.tms.jarBundling)
    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
    mavenLocal()
}

dependencies {
    implementation(managed.jackson.datatypeJsr310)
    implementation(managed.kafka.clients)
    implementation(managed.kotlinLogging)
    implementation(managed.ktor.serialization.jackson)
    implementation(managed.ktor.server.auth)
    implementation(managed.ktor.server.contentNegotiation)
    implementation(managed.ktor.server.cors)
    implementation(managed.ktor.server.defaultHeaders)
    implementation(managed.ktor.server.netty)
    implementation(managed.tms.kafka.producerUtils)
    implementation(managed.tms.ktorTokenSupport.userToken.verification)
    implementation(managed.tms.ktorTokenSupport.userToken.loginRoutes)
    implementation(managed.tms.commonLib.utils)

    implementation(libs.utkast.builder)
    implementation(libs.sulky.ulid)
    implementation(libs.varsel.kotlin.builder)
    implementation(libs.varsel.java.builder)
    implementation(libs.statuskort.kotlin.builder)

    runtimeOnly(managed.ktor.client.apache5)
    runtimeOnly(managed.logback.encoder)

    testImplementation(managed.junit.api)
    testImplementation(managed.kotest.assertion)
    testImplementation(managed.ktor.client.contentNegotiation)
    testImplementation(managed.mockk)
    testImplementation(managed.ktor.test.server)
    testImplementation(managed.tms.ktorTokenSupport.userToken.verificationMock)

    testRuntimeOnly(managed.junit.launcher)
    testRuntimeOnly(managed.junit.engine)
}

application {
    mainClass.set("no.nav.tms.eventtestproducer.setup.AppKt")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
