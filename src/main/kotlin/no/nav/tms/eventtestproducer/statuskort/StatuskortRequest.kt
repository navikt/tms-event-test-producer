package no.nav.tms.eventtestproducer.statuskort

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class StatuskortRequest(
    val type: String? = null,
    val statuskortId: String? = null,
    val tjeneste: String? = null,
    val innhold: Innhold? = null,
)

data class Innhold(
    val nb: Tekstinnhold,
    val nn: Tekstinnhold,
    val en: Tekstinnhold
)

data class Tekstinnhold(
    val link: String,
    val tittel: String,
    val beskrivelse: String,
)


