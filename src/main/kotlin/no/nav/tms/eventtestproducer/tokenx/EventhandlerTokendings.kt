package no.nav.tms.eventtestproducer.tokenx

import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class EventhandlerTokendings(
    private val tokendingsService: TokendingsService,
    private val eventhandlerClientId: String
) {
    suspend fun exchangeToken(innloggetBruker: IdportenUser): AccessToken {
        return AccessToken(tokendingsService.exchangeToken(innloggetBruker.tokenString, eventhandlerClientId))
    }

}
