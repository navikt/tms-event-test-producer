package no.nav.tms.eventtestproducer.setup

import no.nav.tms.common.util.config.BooleanEnvVar.getEnvVarAsBoolean
import no.nav.tms.common.util.config.StringEnvVar.getEnvVar


data class Environment(
    val namespace: String = getEnvVar("NAIS_NAMESPACE"),
    val appnavn: String = "tms-event-test-producer",
    val enableApi: Boolean = getEnvVarAsBoolean("ENABLE_API", false),
    val beskjedInputTopicName: String = getEnvVar("OPEN_INPUT_BESKJED_TOPIC"),
    val oppgaveInputTopicName: String = getEnvVar("OPEN_INPUT_OPPGAVE_TOPIC"),
    val innboksInputTopicName: String = getEnvVar("OPEN_INPUT_INNBOKS_TOPIC"),
    val kafkaBrokers: String = getEnvVar("KAFKA_BROKERS"),
    val kafkaSchemaRegistry: String = getEnvVar("KAFKA_SCHEMA_REGISTRY"),
    val securityVars: SecurityVars = SecurityVars(),
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
    val corsAllowedSchemes: String = getEnvVar("CORS_ALLOWED_SCHEMES", "https"),
    val utkastTopicName: String = "min-side.aapen-utkast-v1",
    val brukervarselTopicName: String = "min-side.aapen-brukervarsel-v1",
    val soknadskvitteringTopicName: String = "min-side.aapen-soknadskvittering-v1"
)

data class SecurityVars(
    val kafkaTruststorePath: String = getEnvVar("KAFKA_TRUSTSTORE_PATH"),
    val kafkaKeystorePath: String = getEnvVar("KAFKA_KEYSTORE_PATH"),
    val kafkaCredstorePassword: String = getEnvVar("KAFKA_CREDSTORE_PASSWORD"),
    val kafkaSchemaRegistryUser: String = getEnvVar("KAFKA_SCHEMA_REGISTRY_USER"),
    val kafkaSchemaRegistryPassword: String = getEnvVar("KAFKA_SCHEMA_REGISTRY_PASSWORD")
)
