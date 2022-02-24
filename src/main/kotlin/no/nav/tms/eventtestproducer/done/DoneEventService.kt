package no.nav.tms.eventtestproducer.done

import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.tms.eventtestproducer.common.HandlerConsumer
import no.nav.tms.eventtestproducer.common.InnloggetBruker
import no.nav.tms.eventtestproducer.tokenx.EventhandlerTokendings

class DoneEventService(
        private val handlerConsumer: HandlerConsumer,
        private val eventhandlerTokendings: EventhandlerTokendings,
        private val doneProducer: DoneProducer
) {

    suspend fun markAllBrukernotifikasjonerAsDone(innloggetBruker: InnloggetBruker) {
        val exchangedToken = eventhandlerTokendings.exchangeToken(innloggetBruker)
        val activeBeskjedEvents = handlerConsumer.getActiveEvents(Eventtype.BESKJED, exchangedToken)
        val activeOppgaveEvents = handlerConsumer.getActiveEvents(Eventtype.OPPGAVE, exchangedToken)
        val activeInnboksEvents = handlerConsumer.getActiveEvents(Eventtype.INNBOKS, exchangedToken)
        val alleBrukernotifikasjoner = activeBeskjedEvents + activeOppgaveEvents + activeInnboksEvents

        alleBrukernotifikasjoner.forEach { brukernotifikasjon ->
            doneProducer.produceDoneEventForSpecifiedEvent(innloggetBruker, brukernotifikasjon)
        }
    }
}
