package no.nav.tms.eventtestproducer.varsel

import java.time.ZonedDateTime

data class ProduceVarselDto(
    val type: String,
    val tekst: String,
    val spraak: String,
    val link: String?,
    val tekster: List<TekstDto> = emptyList(),
    val eksternVarsling: Boolean = false,
    val prefererteKanaler: List<String> = emptyList(),
    val aktivFremTil: ZonedDateTime? = null,
    val epostVarslingstekst: String? = null,
    val epostVarslingstittel: String? = null,
    val smsVarslingstekst: String? = null
)

data class TekstDto(
    val spraak: String,
    val tekst: String,
    val default: Boolean = false
)

