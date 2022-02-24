package no.nav.tms.eventtestproducer.common

import io.ktor.client.*
import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype.*
import no.nav.tms.eventtestproducer.beskjed.Beskjed
import no.nav.tms.eventtestproducer.innboks.Innboks
import no.nav.tms.eventtestproducer.oppgave.Oppgave
import no.nav.tms.eventtestproducer.tokenx.AccessToken
import java.net.URL

class HandlerConsumer(
    private val client: HttpClient,
    private val eventhandlerBaseURL: URL) {

    suspend fun getActiveBeskjedEvents(accessToken:AccessToken): List<Beskjed> {
        val pathToEndpoint = URL("$eventhandlerBaseURL/fetch/beskjed/aktive")
        return client.get(pathToEndpoint, accessToken)
    }

    suspend fun getActiveOppgaveEvents(accessToken:AccessToken): List<Oppgave> {
        val pathToEndpoint = URL("$eventhandlerBaseURL/fetch/oppgave/aktive")
        return client.get(pathToEndpoint, accessToken)
    }

    suspend fun getActiveInnboksEvents(accessToken:AccessToken): List<Innboks> {
        val pathToEndpoint = URL("$eventhandlerBaseURL/fetch/innboks/aktive")
        return client.get(pathToEndpoint, accessToken)
    }
}
