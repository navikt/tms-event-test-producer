package no.nav.tms.eventtestproducer.common

import java.time.ZonedDateTime

interface Brukernotifikasjon {
    val id: Int
    val systembruker: String
    val eventTidspunkt: ZonedDateTime
    val fodselsnummer: String
    val eventId: String
    val grupperingsId: String
    val sikkerhetsnivaa: Int
    val sistOppdatert: ZonedDateTime
    val aktiv: Boolean
    val tekst: String
    val link: String?
}
