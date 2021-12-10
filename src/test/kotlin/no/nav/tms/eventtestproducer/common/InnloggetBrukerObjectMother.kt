package no.nav.tms.eventtestproducer.common

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import no.nav.security.token.support.core.jwt.JwtToken
import java.security.Key

object InnloggetBrukerObjectMother {

    private val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun createInnloggetBruker(): InnloggetBruker {
        val ident = "12345"
        return createInnloggetBruker(ident)
    }

    fun createInnloggetBruker(ident: String): InnloggetBruker {
        val innloggingsnivaa = 4
        return createInnloggetBruker(ident, innloggingsnivaa)
    }

    fun createInnloggetBruker(ident: String, innloggingsnivaa : Int): InnloggetBruker {
        val jws = Jwts.builder()
                .setSubject(ident)
                .addClaims(mutableMapOf(Pair("acr", "Level$innloggingsnivaa")) as Map<String, Any>?)
                .signWith(key).compact()
        val token = JwtToken(jws)
        return InnloggetBruker(ident, innloggingsnivaa, token.tokenAsString)
    }

}
