package no.nav.tms.eventtestproducer.beskjed

import java.time.LocalDateTime

class ProduceBeskjedDto(val tekst: String,
                        val link: String?,
                        val grupperingsid: String,
                        val eksternVarsling: Boolean = false,
                        val prefererteKanaler: List<String> = emptyList(),
                        val synligFremTil: LocalDateTime? = null,
                        val epostVarslingstekst: String? = null,
                        val epostVarslingstittel: String? = null,
                        val smsVarslingstekst: String? = null
) {
    override fun toString(): String {
        return "ProduceBeskjedDto{tekst='$tekst', link='$link', grupperingsid='$grupperingsid', eksternVarsling='$eksternVarsling', synligFremTil='$synligFremTil', prefererteKanaler='$prefererteKanaler', epostVarslingstekst='$epostVarslingstekst', epostVarslingstittel='$epostVarslingstittel', smsVarslingstekst='$smsVarslingstekst'}"
    }
}


