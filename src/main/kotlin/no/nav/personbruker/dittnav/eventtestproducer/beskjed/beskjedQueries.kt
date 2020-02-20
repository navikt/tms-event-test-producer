package no.nav.personbruker.dittnav.eventtestproducer.beskjed

import Beskjed
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBruker
import java.sql.Connection
import java.sql.ResultSet
import java.time.ZoneId
import java.time.ZonedDateTime

fun Connection.getAllBeskjedByFodselsnummer(innloggetBruker: InnloggetBruker): List<Beskjed> =
        prepareStatement("""SELECT * FROM BESKJED WHERE fodselsnummer = ?""")
                .use {
                    it.setString(1, innloggetBruker.getIdent())
                    it.executeQuery().list {
                        toBeskjed()
                    }
                }

fun Connection.getBeskjedByFodselsnummer(innloggetBruker: InnloggetBruker): List<Beskjed> =
        prepareStatement("""SELECT * FROM BESKJED WHERE fodselsnummer = ? AND aktiv = true""")
                .use {
                    it.setString(1, innloggetBruker.getIdent())
                    it.executeQuery().list {
                        toBeskjed()
                    }
                }

private fun ResultSet.toBeskjed(): Beskjed {
    return Beskjed(
            id = getInt("id"),
            produsent = getString("produsent"),
            eventTidspunkt = ZonedDateTime.ofInstant(getTimestamp("eventTidspunkt").toInstant(), ZoneId.of("Europe/Oslo")),
            fodselsnummer = getString("fodselsnummer"),
            eventId = getString("eventId"),
            grupperingsId = getString("grupperingsId"),
            tekst = getString("tekst"),
            link = getString("link"),
            sikkerhetsnivaa = getInt("sikkerhetsnivaa"),
            sistOppdatert = ZonedDateTime.ofInstant(getTimestamp("sistOppdatert").toInstant(), ZoneId.of("Europe/Oslo")),
            aktiv = getBoolean("aktiv")
    )
}

private fun <T> ResultSet.list(result: ResultSet.() -> T): List<T> =
        mutableListOf<T>().apply {
            while (next()) {
                add(result())
            }
        }
