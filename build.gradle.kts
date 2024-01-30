plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm").version(Kotlin.version)
    kotlin("plugin.allopen").version(Kotlin.version)
    kotlin("plugin.serialization").version(Kotlin.version)

    id(Shadow.pluginId) version (Shadow.version)
    // Apply the application plugin to add support for building a CLI application.
    application
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

repositories {
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
    mavenCentral()
    maven("https://packages.confluent.io/maven")
    mavenLocal()
}

dependencies {
    implementation("no.nav.tms:brukernotifikasjon-schemas:2.6.0")
    implementation(Avro.avroSerializer)
    implementation(JacksonDatatype.datatypeJsr310)
    implementation(Kafka.clients)
    implementation(KotlinLogging.logging)
    implementation(Ktor.Client.apache)
    implementation(Ktor.Client.contentNegotiation)
    implementation(Ktor.Serialization.jackson)
    implementation(Ktor.Server.auth)
    implementation(Ktor.Server.authJwt)
    implementation(Ktor.Server.contentNegotiation)
    implementation(Ktor.Server.cors)
    implementation(Ktor.Server.defaultHeaders)
    implementation(Ktor.Server.netty)
    implementation(Logstash.logbackEncoder)
    implementation(Prometheus.logback)
    implementation(SulkyUlid.sulkyUlid)
    implementation(TmsKtorTokenSupport.idportenSidecar)
    implementation(Utkast.builder)
    implementation(TmsVarselBuilder.kotlinBuilder)
    implementation(TmsVarselBuilder.javabuilder)
    implementation(TmsCommonLib.utils)

    testImplementation(kotlin("test-junit5"))
    testImplementation(Jjwt.api)
    testImplementation(Jjwt.impl)
    testImplementation(Jjwt.orgjson)
    testImplementation(Junit.api)
    testImplementation(Junit.engine)
    testImplementation(Kafka.kafka_2_12)
    testImplementation(Kluent.kluent)
    testImplementation(Ktor.Test.clientMock)
    testImplementation(Mockk.mockk)
    testImplementation(Ktor.Test.serverTestHost)
    testImplementation(TmsKtorTokenSupport.idportenSidecarMock)
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

// TODO: Fjern følgende work around i ny versjon av Shadow-pluginet:
// Skal være løst i denne: https://github.com/johnrengelman/shadow/pull/612
project.setProperty("mainClassName", application.mainClass.get())
apply(plugin = Shadow.pluginId)
