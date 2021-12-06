package no.nav.tms.eventtestproducer.oppgave

import no.nav.tms.eventtestproducer.common.Brukernotifikasjon
import java.time.ZonedDateTime

data class Oppgave(
        override val id: Int,
        override val fodselsnummer: String,
        override val grupperingsId: String,
        override val eventId: String,
        override val eventTidspunkt: ZonedDateTime,
        override val systembruker: String,
        override val sikkerhetsnivaa: Int,
        override val sistOppdatert: ZonedDateTime,
        override val tekst: String,
        override val link: String,
        override val aktiv: Boolean,
        val eksternVarsling: Boolean,
        val prefererteKanaler: List<String>
) : Brukernotifikasjon {

    override fun toString(): String {
        return "Oppgave(" +
                "id=$id, " +
                "fodselsnummer=***, " +
                "grupperingsId=$grupperingsId, " +
                "eventId=$eventId, " +
                "eventTidspunkt=$eventTidspunkt, " +
                "systembruker=***, " +
                "sikkerhetsnivaa=$sikkerhetsnivaa, " +
                "sistOppdatert=$sistOppdatert, " +
                "tekst=***, " +
                "link=***, " +
                "aktiv=$aktiv, " +
                "eksternVarsling=$eksternVarsling, " +
                "prefererteKanaler=$prefererteKanaler"
    }
}
