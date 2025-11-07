package no.nav.tms.eventtestproducer.setup

import no.nav.tms.common.util.config.BooleanEnvVar.getEnvVarAsBoolean
import no.nav.tms.common.util.config.StringEnvVar.getEnvVar


data class Environment(
    val namespace: String = getEnvVar("NAIS_NAMESPACE"),
    val appnavn: String = "tms-event-test-producer",
    val enableApi: Boolean = getEnvVarAsBoolean("ENABLE_API", false),
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
    val corsAllowedSchemes: String = getEnvVar("CORS_ALLOWED_SCHEMES", "https"),
    val utkastTopicName: String = "min-side.aapen-utkast-v1",
    val brukervarselTopicName: String = "min-side.aapen-brukervarsel-v1"
)
