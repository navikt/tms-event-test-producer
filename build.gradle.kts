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
    mavenCentral()
    maven("https://packages.confluent.io/maven")
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.navikt:brukernotifikasjon-schemas:2.5.1")
    implementation(DittNAVCommonLib.utils)
    implementation(Hikari.cp)
    implementation(Kafka.clients)
    implementation(Avro.avroSerializer)
    implementation(Ktor2.Client.apache)
    implementation(Ktor2.Client.contentNegotiation)
    implementation(Ktor2.Serialization.kotlinX)
    implementation(Ktor2.Server.auth)
    implementation(Ktor2.Server.authJwt)
    implementation(Ktor2.Server.contentNegotiation)
    implementation(Ktor2.Server.cors)
    implementation(Ktor2.Server.defaultHeaders)
    implementation(Ktor2.Server.netty)
    implementation(Kotlinx.datetime)
    implementation(Logback.classic)
    implementation(Logstash.logbackEncoder)
    implementation(Prometheus.logback)
    implementation(TmsKtorTokenSupport.idportenSidecar)
    implementation(SulkyUlid.sulkyUlid)
    implementation("com.github.navikt:tms-utkast:20230203100430-ecf5208")

    testImplementation(kotlin("test-junit5"))
    testImplementation(Bouncycastle.bcprovJdk15on)
    testImplementation(H2Database.h2)
    testImplementation(Jjwt.api)
    testImplementation(Jjwt.impl)
    testImplementation(Jjwt.orgjson)
    testImplementation(Junit.api)
    testImplementation(Junit.engine)
    testImplementation(Kafka.kafka_2_12)
    testImplementation(Kafka.streams)
    testImplementation(Avro.schemaRegistry)
    testImplementation(Kluent.kluent)
    testImplementation(Ktor2.Test.clientMock)
    testImplementation(Mockk.mockk)
    testImplementation(NAV.kafkaEmbedded)
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
