package no.nav.tms.eventtestproducer.oppgave

import java.time.Instant
import java.time.LocalDateTime

class ProduceOppgaveDto(val tekst: String,
                        val link: String,
                        val grupperingsid: String,
                        val eksternVarsling: Boolean = false,
                        val prefererteKanaler: List<String> = emptyList(),
                        val synligFremTil: LocalDateTime? = null,
                        val epostVarslingstekst: String? = null,
                        val epostVarslingstittel: String? = null,
                        val smsVarslingstekst: String? = null) {
    override fun toString(): String {
        return "ProduceOppgaveDto{tekst='$tekst', link='$link', grupperingsid='$grupperingsid', eksternVarsling='$eksternVarsling', prefererteKanaler='$prefererteKanaler', synligFremTil='$synligFremTil', epostVarslingstekst='$epostVarslingstekst', epostVarslingstittel='$epostVarslingstittel', smsVarslingstekst='$smsVarslingstekst'}"
    }
}
