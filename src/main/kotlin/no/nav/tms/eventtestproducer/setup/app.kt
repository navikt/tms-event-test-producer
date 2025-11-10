package no.nav.tms.eventtestproducer.setup

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import no.nav.tms.eventtestproducer.microfrontend.microfrontedApi
import no.nav.tms.eventtestproducer.utkast.utkastApi
import no.nav.tms.eventtestproducer.varsel.varselApi
import no.nav.tms.token.support.idporten.sidecar.IdPortenLogin
import no.nav.tms.token.support.idporten.sidecar.LevelOfAssurance.SUBSTANTIAL
import no.nav.tms.token.support.idporten.sidecar.idPorten
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUserFactory
import java.text.DateFormat

fun main() {
    val appContext = ApplicationContext()

    embeddedServer(
        factory = Netty,
        configure = {
            connector {
                port = 8080
            }
        },
        module = {
            rootPath = "tms-event-test-producer"

            testProducerApi(appContext)
        }
    ).start(wait = true)
}

fun Application.testProducerApi(appContext: ApplicationContext) {
    install(DefaultHeaders)

    install(ContentNegotiation) {
        jackson {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            registerModule(JavaTimeModule())
            dateFormat = DateFormat.getDateTimeInstance()
        }
    }

    authentication {
        idPorten {
            setAsDefault = true
            levelOfAssurance = SUBSTANTIAL
        }
    }

    install(IdPortenLogin)

    install(CORS) {
        allowHost(appContext.environment.corsAllowedOrigins, listOf(appContext.environment.corsAllowedSchemes))
        allowCredentials = true
        allowHeader(HttpHeaders.ContentType)
    }

    routing {
        healthApi()
        authenticate {
            if(appContext.environment.enableApi) {
                utkastApi(appContext.utkastRapidProducer, appContext.utkastMultiProducer)
                microfrontedApi(appContext.microfrontendProducer)
                varselApi(appContext.varselProducer)
            }
        }
    }
}

val RoutingContext.innloggetBruker: IdportenUser
    get() = IdportenUserFactory.createIdportenUser(call)

