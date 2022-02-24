package no.nav.tms.eventtestproducer.common

interface Brukernotifikasjon {
    val id: Int
    val systembruker: String
    val eventTidspunkt: String
    val fodselsnummer: String
    val eventId: String
    val grupperingsId: String
    val sikkerhetsnivaa: Int
    val sistOppdatert: String
    val aktiv: Boolean
    val tekst: String
    val link: String?
}
