package no.nav.tms.eventtestproducer.utkast

import kotlinx.serialization.Serializable

@Serializable
data class UtkastCreate(
    val utkastId: String,
    val tittel: String,
    val link: String,
    val tittelI18n: Map<String, String>? = null
)


@Serializable
data class UtkastUpdate(
    val utkastId: String,
    val tittel: String? = null,
    val link: String? = null,
    val tittelI18n: Map<String, String>? = null
)


@Serializable
data class UtkastDelete(
    val utkastId: String
)

