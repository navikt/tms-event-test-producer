package no.nav.tms.eventtestproducer.config

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.pipeline.*
import no.nav.security.token.support.ktor.tokenValidationSupport
import no.nav.tms.eventtestproducer.beskjed.beskjedApi
import no.nav.tms.eventtestproducer.common.InnloggetBruker
import no.nav.tms.eventtestproducer.common.InnloggetBrukerFactory
import no.nav.tms.eventtestproducer.common.healthApi
import no.nav.tms.eventtestproducer.done.doneApi
import no.nav.tms.eventtestproducer.innboks.innboksApi
import no.nav.tms.eventtestproducer.oppgave.oppgaveApi
import no.nav.tms.token.support.tokenx.validation.installTokenXAuth

fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {
    install(DefaultHeaders)

    install(ContentNegotiation) {
        json()
    }

    installTokenXAuth() {
        setAsDefault = true
    }

    install(CORS) {
        host(appContext.environment.corsAllowedOrigins, listOf(appContext.environment.corsAllowedSchemes))
        allowCredentials = true
        header(HttpHeaders.ContentType)
    }

    routing {
        healthApi()
        authenticate {
            if(appContext.environment.enableApi) {
                oppgaveApi(appContext.oppgaveProducer)
                beskjedApi(appContext.beskjedProducer)
                innboksApi(appContext.innboksProducer)
                doneApi(appContext.doneEventService)
            }
        }

        authenticate {
            get("/beskjed") {
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    configureShutdownHook(appContext)
}

val PipelineContext<Unit, ApplicationCall>.innloggetBruker: InnloggetBruker
    get() = InnloggetBrukerFactory.createNewInnloggetBruker(call.authentication.principal())

private fun Application.configureShutdownHook(appContext: ApplicationContext) {
    environment.monitor.subscribe(ApplicationStopPreparing) {
        appContext.kafkaProducerBeskjed.flushAndClose()
        appContext.kafkaProducerDone.flushAndClose()
        appContext.kafkaProducerInnboks.flushAndClose()
        appContext.kafkaProducerOppgave.flushAndClose()
    }
}
