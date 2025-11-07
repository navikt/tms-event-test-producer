package no.nav.tms.eventtestproducer.varsel

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class ProduceVarselRequest(
    var type: String = "lateinit",
    val tekst: String,
    val spraak: String,
    val link: String?,
    val tekster: List<TekstDto> = emptyList(),
    val eksternVarsling: Boolean = false,
    val preferertKanal: String? = null,
    val utsettSendingTil: ZonedDateTime? = null,
    val kanBatches: Boolean? = null,
    val epostVarslingstekst: String? = null,
    val epostVarslingstittel: String? = null,
    val smsVarslingstekst: String? = null,
    val javaBuilder: Boolean = false,

    @JsonProperty("aktivFremTil") private val _aktivFremTil: ZonedDateTime? = null,
    @JsonProperty("synligFremTil") private val _synligFremTil: LocalDateTime? = null,
) {
    val aktivFremTil = _aktivFremTil ?: _synligFremTil?.atZone(ZoneId.of("Z"))
}

data class TekstDto(
    val spraak: String,
    val tekst: String,
    val default: Boolean = false
)

