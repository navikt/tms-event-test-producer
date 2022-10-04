package no.nav.tms.eventtestproducer.common

import com.auth0.jwt.JWT
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import java.security.Key
import java.time.ZonedDateTime
import java.util.*

object InnloggetBrukerObjectMother {

    private val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun createInnloggetBruker(): IdportenUser {
        val ident = "12345"
        return createInnloggetBruker(ident)
    }

    fun createInnloggetBruker(ident: String): IdportenUser {
        val innloggingsnivaa = 4
        return createInnloggetBruker(ident, innloggingsnivaa)
    }

    fun createInnloggetBruker(ident: String, innloggingsnivaa: Int): IdportenUser {
        val inTwoMinutes = ZonedDateTime.now().plusMinutes(2)
        return createInnloggetBruker(ident, innloggingsnivaa, inTwoMinutes)
    }

    fun createInnloggetBruker(ident: String, innloggingsnivaa: Int, tokenUtlopstidspunkt: ZonedDateTime): IdportenUser {
        val jws = Jwts.builder()
            .setSubject(ident)
            .addClaims(mutableMapOf(Pair("acr", "Level$innloggingsnivaa")) as Map<String, Any>?)
            .setExpiration(Date.from(tokenUtlopstidspunkt.toInstant()))
            .signWith(key).compact()
        val token = JWT.decode(jws)
        val expirationTime = token.expiresAt.toInstant()
        return IdportenUser(ident, innloggingsnivaa, expirationTime, token)
    }
}
