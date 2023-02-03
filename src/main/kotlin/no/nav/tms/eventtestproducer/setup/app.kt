package no.nav.tms.eventtestproducer.setup

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import no.nav.tms.eventtestproducer.beskjed.beskjedApi
import no.nav.tms.eventtestproducer.innboks.innboksApi
import no.nav.tms.eventtestproducer.oppgave.oppgaveApi
import no.nav.tms.eventtestproducer.utkast.utkastApi
import no.nav.tms.token.support.idporten.sidecar.installIdPortenAuth
import no.nav.tms.token.support.idporten.sidecar.LoginLevel.LEVEL_3

fun main() {
    val appContext = ApplicationContext()

    embeddedServer(Netty, port = appContext.environment.port) {
        testProducerApi(appContext)
    }.start(wait = true)
}

fun Application.testProducerApi(appContext: ApplicationContext) {
    install(DefaultHeaders)

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    installIdPortenAuth {
        setAsDefault = true
        loginLevel = LEVEL_3
    }

    install(CORS) {
        allowHost(appContext.environment.corsAllowedOrigins, listOf(appContext.environment.corsAllowedSchemes))
        allowCredentials = true
        allowHeader(HttpHeaders.ContentType)
    }

    routing {
        route("/tms-event-test-producer") {
            healthApi()
            authenticate {
                if(appContext.environment.enableApi) {
                    oppgaveApi(appContext.oppgaveProducer)
                    beskjedApi(appContext.beskjedProducer)
                    innboksApi(appContext.innboksProducer)
                    utkastApi(appContext.utkastRapidProducer, appContext.utkastMultiProducer)
                }
            }
        }
    }

    configureShutdownHook(appContext)
}

private fun Application.configureShutdownHook(appContext: ApplicationContext) {
    environment.monitor.subscribe(ApplicationStopPreparing) {
        appContext.kafkaProducerBeskjed.flushAndClose()
        appContext.kafkaProducerInnboks.flushAndClose()
        appContext.kafkaProducerOppgave.flushAndClose()
    }
}
