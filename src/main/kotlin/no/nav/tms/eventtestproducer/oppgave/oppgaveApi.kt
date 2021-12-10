package no.nav.tms.eventtestproducer.oppgave

import io.ktor.routing.Route
import io.ktor.routing.post
import no.nav.tms.eventtestproducer.config.innloggetBruker
import no.nav.tms.eventtestproducer.config.respondForParameterType

fun Route.oppgaveApi(oppgaveProducer: OppgaveProducer) {

    post("/produce/oppgave") {
        this.respondForParameterType<ProduceOppgaveDto> { oppgaveDto ->
            oppgaveProducer.produceOppgaveEventForIdent(innloggetBruker, oppgaveDto)
            "Et oppgave-event for for brukeren: $innloggetBruker har blitt lagt på kafka."
        }
    }

}
