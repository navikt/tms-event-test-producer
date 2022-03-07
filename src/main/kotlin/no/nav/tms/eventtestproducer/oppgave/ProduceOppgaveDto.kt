package no.nav.tms.eventtestproducer.oppgave

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class ProduceOppgaveDto(val tekst: String,
                        val link: String,
                        val grupperingsid: String,
                        val eksternVarsling: Boolean = false,
                        val prefererteKanaler: List<String> = emptyList(),
                        val synligFremTil: Instant? = null,
                        val epostVarslingstekst: String? = null,
                        val epostVarslingstittel: String? = null,
                        val smsVarslingstekst: String? = null) {
    override fun toString(): String {
        return "ProduceOppgaveDto{tekst='$tekst', link='$link', grupperingsid='$grupperingsid', eksternVarsling='$eksternVarsling', prefererteKanaler='$prefererteKanaler', synligFremTil='$synligFremTil', epostVarslingstekst='$epostVarslingstekst', epostVarslingstittel='$epostVarslingstittel', smsVarslingstekst='$smsVarslingstekst'}"
    }
}
