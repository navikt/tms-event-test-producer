package no.nav.tms.eventtestproducer.utkast

import kotlinx.serialization.Serializable

@Serializable
data class UtkastCreate(
    val eventId: String,
    val tittel: String,
    val link: String
)


@Serializable
data class UtkastUpdate(
    val eventId: String,
    val tittel: String? = null,
    val link: String? = null
)


@Serializable
data class UtkastDelete(
    val eventId: String,
)

