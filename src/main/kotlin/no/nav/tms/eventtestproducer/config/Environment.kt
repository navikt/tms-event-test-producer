package no.nav.tms.eventtestproducer.config

import no.nav.personbruker.dittnav.common.util.config.BooleanEnvVar.getEnvVarAsBoolean
import no.nav.personbruker.dittnav.common.util.config.StringEnvVar.getEnvVar
import no.nav.personbruker.dittnav.common.util.config.UrlEnvVar.getEnvVarAsURL
import no.nav.tms.eventtestproducer.config.ConfigUtil.isCurrentlyRunningOnNais
import java.net.URL

data class Environment(
    val namespace: String = getEnvVar("NAIS_NAMESPACE"),
    val appnavn: String = "tms-event-test-producer",
    val beskjedInputTopicName: String = getEnvVar("OPEN_INPUT_BESKJED_TOPIC"),
    val oppgaveInputTopicName: String = getEnvVar("OPEN_INPUT_OPPGAVE_TOPIC"),
    val innboksInputTopicName: String = getEnvVar("OPEN_INPUT_INNBOKS_TOPIC"),
    val doneInputTopicName: String = getEnvVar("OPEN_INPUT_DONE_TOPIC"),
    val aivenBrokers: String = getEnvVar("KAFKA_BROKERS"),
    val aivenSchemaRegistry: String = getEnvVar("KAFKA_SCHEMA_REGISTRY"),
    val securityConfig: SecurityConfig = SecurityConfig(isCurrentlyRunningOnNais()),
    val eventHandlerURL: URL = getEnvVarAsURL("EVENT_HANDLER_URL", trimTrailingSlash = true),
    val eventhandlerClientId: String = getEnvVar("EVENTHANDLER_CLIENT_ID"),
    val enableApi: Boolean = getEnvVarAsBoolean("ENABLE_API", false),
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
    val corsAllowedSchemes: String = getEnvVar("CORS_ALLOWED_SCHEMES", "https")
)

data class SecurityConfig(
    val enabled: Boolean,

    val variables: SecurityVars? = if (enabled) {
        SecurityVars()
    } else {
        null
    }
)

data class SecurityVars(
    val aivenTruststorePath: String = getEnvVar("KAFKA_TRUSTSTORE_PATH"),
    val aivenKeystorePath: String = getEnvVar("KAFKA_KEYSTORE_PATH"),
    val aivenCredstorePassword: String = getEnvVar("KAFKA_CREDSTORE_PASSWORD"),
    val aivenSchemaRegistryUser: String = getEnvVar("KAFKA_SCHEMA_REGISTRY_USER"),
    val aivenSchemaRegistryPassword: String = getEnvVar("KAFKA_SCHEMA_REGISTRY_PASSWORD")
)

fun getEnvVar(varName: String): String {
    return System.getenv(varName)
            ?: throw IllegalArgumentException("Appen kan ikke starte uten av miljøvariabelen $varName er satt.")
}
