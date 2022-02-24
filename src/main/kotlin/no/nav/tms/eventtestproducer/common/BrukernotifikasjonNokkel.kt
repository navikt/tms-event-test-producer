package no.nav.tms.eventtestproducer.common

import kotlinx.serialization.Serializable

@Serializable
data class BrukernotifikasjonNokkel (
    val fodselsnummer: String,
    val eventId: String
)
