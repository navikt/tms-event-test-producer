package no.nav.personbruker.dittnav.eventtestproducer.common.util

import no.nav.personbruker.dittnav.eventtestproducer.config.Environment
import java.net.URL

fun createPropertiesForTestEnvironment(): Environment {
    return Environment(
        aivenBrokers = "localhost:29092",
        aivenSchemaRegistry = "http://localhost:8081",
        namespace = "local",
        appnavn = "dittnav-event-test-producer",
        corsAllowedOrigins = "localhost:9002",
        beskjedInputTopicName = "ikkeIBruk",
        oppgaveInputTopicName = "ikkeIBruk",
        innboksInputTopicName = "ikkeIBruk",
        statusoppdateringInputTopicName = "ikkeIBruk",
        doneInputTopicName = "ikkeIBruk",
        eventHandlerURL = URL("http://ikkeIBruk"),
        eventhandlerClientId = "ikkeIBruk"
    )
}
