package no.nav.tms.eventtestproducer.common.util

import no.nav.tms.eventtestproducer.config.Environment
import java.net.URL

fun createPropertiesForTestEnvironment(): Environment {
    return Environment(
        aivenBrokers = "localhost:29092",
        aivenSchemaRegistry = "http://localhost:8081",
        namespace = "local",
        appnavn = "tms-event-test-producer",
        beskjedInputTopicName = "ikkeIBruk",
        oppgaveInputTopicName = "ikkeIBruk",
        innboksInputTopicName = "ikkeIBruk",
        doneInputTopicName = "ikkeIBruk",
        eventHandlerURL = URL("http://ikkeIBruk"),
        eventhandlerClientId = "ikkeIBruk",
        corsAllowedOrigins = "localhost",
        utkastTopicName = "ikkeIBruk"
    )
}
