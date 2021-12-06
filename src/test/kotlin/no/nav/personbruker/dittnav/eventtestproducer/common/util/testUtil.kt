package no.nav.personbruker.dittnav.eventtestproducer.common.util

import no.nav.personbruker.dittnav.eventtestproducer.config.Environment

fun createPropertiesForTestEnvironment(): Environment {
    return Environment(
        aivenBrokers = "localhost:29092",
        aivenSchemaRegistry = "http://localhost:8081",
        dbHost = "localhost:5432",
        dbName = "dittnav-event-cache-preprod",
        dbMountPath = "notUsedOnLocalhost",
        namespace = "local",
        appnavn = "dittnav-event-test-producer",
        corsAllowedOrigins = "localhost:9002",
        beskjedInputTopicName = "ikkeIBruk",
        oppgaveInputTopicName = "ikkeIBruk",
        innboksInputTopicName = "ikkeIBruk",
        statusoppdateringInputTopicName = "ikkeIBruk",
        doneInputTopicName = "ikkeIBruk"
    )
}
