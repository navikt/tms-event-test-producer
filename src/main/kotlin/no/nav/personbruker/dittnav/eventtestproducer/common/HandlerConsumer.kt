package no.nav.personbruker.dittnav.eventtestproducer.common

import io.ktor.client.*
import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.personbruker.dittnav.eventtestproducer.tokenx.AccessToken
import java.net.URL

class HandlerConsumer(
    private val client: HttpClient,
    private val eventhandlerBaseURL: URL) {

    suspend fun <T> getActiveEvents(eventtype: Eventtype, accessToken:AccessToken): List<T> {
        val pathToEndpoint = URL("$eventhandlerBaseURL/${eventtype.name}/aktive")
        return client.get(pathToEndpoint, accessToken)
    }
}
