package no.nav.tms.eventtestproducer.statusoppdatering

import kotlinx.serialization.Serializable

@Serializable
data class Statusoppdatering(
        val id: Int,
        val systembruker: String,
        val eventId: String,
        val eventTidspunkt: String,
        val fodselsnummer: String,
        val grupperingsId: String,
        val link: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: String,
        val statusGlobal: String,
        val statusIntern: String?,
        val sakstema: String
) {
    override fun toString(): String {
        return "Statusoppdatering(" +
                "id=$id, " +
                "systembruker=***, " +
                "eventId=$eventId, " +
                "eventTidspunkt=$eventTidspunkt, " +
                "fodselsnummer=***, " +
                "grupperingsId=$grupperingsId, " +
                "link=***, " +
                "sikkerhetsnivaa=$sikkerhetsnivaa, " +
                "sistOppdatert=$sistOppdatert, " +
                "statusGlobal=$statusGlobal, " +
                "statusIntern=$statusIntern, " +
                "sakstema=$sakstema"
    }
}
