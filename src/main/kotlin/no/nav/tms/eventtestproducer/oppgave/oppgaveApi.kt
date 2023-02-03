package no.nav.tms.eventtestproducer.oppgave

import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import no.nav.tms.eventtestproducer.util.innloggetBruker
import no.nav.tms.eventtestproducer.util.respondForParameterType

fun Route.oppgaveApi(oppgaveProducer: OppgaveProducer) {

    post("/produce/oppgave") {
        this.respondForParameterType<ProduceOppgaveDto> { oppgaveDto ->
            oppgaveProducer.produceOppgaveEventForIdent(innloggetBruker, oppgaveDto)
            "Et oppgave-event for for brukeren: $innloggetBruker har blitt lagt på kafka."
        }
    }

}
