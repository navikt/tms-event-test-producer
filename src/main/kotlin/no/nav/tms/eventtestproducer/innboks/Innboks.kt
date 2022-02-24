package no.nav.tms.eventtestproducer.innboks

import kotlinx.serialization.Serializable
import no.nav.tms.eventtestproducer.common.Brukernotifikasjon
import java.time.ZonedDateTime

@Serializable
data class Innboks (
        override val id: Int,
        override val systembruker: String,
        override val eventTidspunkt: String,
        override val fodselsnummer: String,
        override val eventId: String,
        override val grupperingsId: String,
        override val tekst: String,
        override val link: String,
        override val sikkerhetsnivaa: Int,
        override val sistOppdatert: String,
        override val aktiv: Boolean,
        val eksternVarsling: Boolean,
        val prefererteKanaler: List<String>
) : Brukernotifikasjon {

    override fun toString(): String {
        return "Innboks(" +
                "id=$id, " +
                "systembruker=***, " +
                "eventTidspunkt=$eventTidspunkt, " +
                "fodselsnummer=***, " +
                "eventId=$eventId, " +
                "grupperingsId=$grupperingsId, " +
                "tekst=***, " +
                "link=***, " +
                "sikkerhetsnivaa=$sikkerhetsnivaa, " +
                "sistOppdatert=$sistOppdatert, " +
                "aktiv=$aktiv, " +
                "eksternVarsling=$eksternVarsling, " +
                "prefererteKanaler=$prefererteKanaler"
    }
}
