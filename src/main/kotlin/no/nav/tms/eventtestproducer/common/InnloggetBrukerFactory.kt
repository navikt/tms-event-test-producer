package no.nav.tms.eventtestproducer.common

import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.ktor.TokenValidationContextPrincipal

object InnloggetBrukerFactory {

    private val IDENT_CLAIM: IdentityClaim
    private val defaultClaim = IdentityClaim.SUBJECT
    private val oidcIdentityClaimName = "OIDC_CLAIM_CONTAINING_THE_IDENTITY"

    init {
        val identityClaimFromEnvVariable = System.getenv(oidcIdentityClaimName) ?: defaultClaim.claimName
        IDENT_CLAIM = IdentityClaim.fromClaimName(identityClaimFromEnvVariable)
    }

    fun createNewInnloggetBruker(principal: TokenValidationContextPrincipal?): InnloggetBruker {
        val token = principal?.context?.firstValidToken?.get()
                ?: throw Exception("Det ble ikke funnet noe token. Dette skal ikke kunne skje.")

        val ident: String = token.jwtTokenClaims.getStringClaim(IDENT_CLAIM.claimName)
        val innloggingsnivaa = extractInnloggingsnivaa(token)

        return InnloggetBruker(ident, innloggingsnivaa, token.tokenAsString)
    }

    private fun extractInnloggingsnivaa(token: JwtToken): Int {
        val innloggingsnivaaClaim = token.jwtTokenClaims.getStringClaim("acr")

        return when (innloggingsnivaaClaim) {
            "Level3" -> 3
            "Level4" -> 4
            else -> throw Exception("Innloggingsnivå ble ikke funnet. Dette skal ikke kunne skje.")
        }
    }

}
