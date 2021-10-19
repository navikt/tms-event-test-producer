package no.nav.personbruker.dittnav.eventtestproducer.config

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.beskjedApi
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBrukerFactory
import no.nav.personbruker.dittnav.eventtestproducer.common.healthApi
import no.nav.personbruker.dittnav.eventtestproducer.done.doneApi
import no.nav.personbruker.dittnav.eventtestproducer.innboks.innboksApi
import no.nav.personbruker.dittnav.eventtestproducer.oppgave.oppgaveApi
import no.nav.personbruker.dittnav.eventtestproducer.statusoppdatering.statusoppdateringApi
import no.nav.personbruker.dittnav.eventtestproducer.ytelsestesting.ytelsestestApi
import no.nav.security.token.support.ktor.tokenValidationSupport

@KtorExperimentalAPI
fun Application.mainModule(appContext: ApplicationContext = ApplicationContext()) {
    DefaultExports.initialize()
    install(DefaultHeaders)

    install(CORS) {
        host(appContext.environment.corsAllowedOrigins, schemes = listOf("https"))
        allowCredentials = true
        header(HttpHeaders.ContentType)
    }

    install(ContentNegotiation) {
        json()
    }

    val config = this.environment.config

    install(Authentication) {
        tokenValidationSupport(config = config)
    }

    routing {
        healthApi()
        authenticate {
            oppgaveApi(appContext.oppgaveProducer)
            beskjedApi(appContext.beskjedProducer)
            innboksApi(appContext.innboksProducer)
            doneApi(appContext.doneEventService)
            statusoppdateringApi(appContext.statusoppdateringProducer)
            ytelsestestApi(appContext.testDataService)
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
        appContext.kafkaProducerStatusoppdatering.flushAndClose()
    }
}
