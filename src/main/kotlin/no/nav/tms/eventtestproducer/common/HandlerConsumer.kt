package no.nav.tms.eventtestproducer.common

import io.ktor.client.*
import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.tms.eventtestproducer.tokenx.AccessToken
import java.net.URL

class HandlerConsumer(
    private val client: HttpClient,
    private val eventhandlerBaseURL: URL) {

    suspend fun getActiveEvents(eventtype: Eventtype, accessToken:AccessToken): List<BrukernotifikasjonNokkel> {
        val pathToEndpoint = URL("$eventhandlerBaseURL/fetch/${eventtype.name.lowercase()}/aktive")
        return client.get(pathToEndpoint, accessToken)
    }
}
