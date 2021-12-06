package no.nav.personbruker.dittnav.eventtestproducer.config

import no.nav.personbruker.dittnav.eventtestproducer.config.ConfigUtil.isCurrentlyRunningOnNais

data class Environment(val dbHost: String = getEnvVar("DB_HOST"),
                       val dbName: String = getEnvVar("DB_NAME"),
                       val dbUser: String = "$dbName-user",
                       val dbReadOnlyUser: String = "$dbName-readonly",
                       val dbUrl: String = "jdbc:postgresql://$dbHost/$dbName",
                       val dbMountPath: String = getEnvVar("DB_MOUNT_PATH"),
                       val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
                       val namespace: String = getEnvVar("NAIS_NAMESPACE"),
                       val appnavn: String = getEnvVar("NAIS_APP_NAME"),
                       val beskjedInputTopicName: String = getEnvVar("OPEN_INPUT_BESKJED_TOPIC"),
                       val oppgaveInputTopicName: String = getEnvVar("OPEN_INPUT_OPPGAVE_TOPIC"),
                       val innboksInputTopicName: String = getEnvVar("OPEN_INPUT_INNBOKS_TOPIC"),
                       val statusoppdateringInputTopicName: String = getEnvVar("OPEN_INPUT_STATUSOPPDATERING_TOPIC"),
                       val doneInputTopicName: String = getEnvVar("OPEN_INPUT_DONE_TOPIC"),
                       val aivenBrokers: String = getEnvVar("KAFKA_BROKERS"),
                       val aivenSchemaRegistry: String = getEnvVar("KAFKA_SCHEMA_REGISTRY"),
                       val securityConfig: SecurityConfig = SecurityConfig(isCurrentlyRunningOnNais())
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
