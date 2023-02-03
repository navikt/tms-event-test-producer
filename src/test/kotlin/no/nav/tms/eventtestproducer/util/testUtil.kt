package no.nav.tms.eventtestproducer.util

import no.nav.tms.eventtestproducer.setup.Environment
import no.nav.tms.eventtestproducer.setup.SecurityVars

fun createPropertiesForTestEnvironment(): Environment {
    return Environment(
        kafkaBrokers = "localhost:29092",
        kafkaSchemaRegistry = "http://localhost:8081",
        namespace = "local",
        appnavn = "tms-event-test-producer",
        beskjedInputTopicName = "ikkeIBruk",
        oppgaveInputTopicName = "ikkeIBruk",
        innboksInputTopicName = "ikkeIBruk",
        corsAllowedOrigins = "localhost",
        utkastTopicName = "ikkeIBruk",
        securityVars = SecurityVars(
            kafkaTruststorePath = "ikkeIBruk",
            kafkaKeystorePath = "ikkeIBruk",
            kafkaCredstorePassword = "ikkeIBruk",
            kafkaSchemaRegistryUser = "ikkeIBruk",
            kafkaSchemaRegistryPassword = "ikkeIBruk"
        )
    )
}
