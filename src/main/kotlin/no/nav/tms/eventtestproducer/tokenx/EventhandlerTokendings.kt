package no.nav.tms.eventtestproducer.tokenx

import no.nav.tms.eventtestproducer.common.InnloggetBruker
import no.nav.tms.token.support.tokendings.exchange.TokendingsService

class EventhandlerTokendings(
    private val tokendingsService: TokendingsService,
    private val eventhandlerClientId: String
) {
    suspend fun exchangeToken(innloggetBruker: InnloggetBruker): AccessToken {
        return AccessToken(tokendingsService.exchangeToken(innloggetBruker.token, eventhandlerClientId))
    }
}
