package no.nav.tms.eventtestproducer.done

import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.tms.eventtestproducer.beskjed.Beskjed
import no.nav.tms.eventtestproducer.common.HandlerConsumer
import no.nav.tms.eventtestproducer.common.InnloggetBruker
import no.nav.tms.eventtestproducer.innboks.Innboks
import no.nav.tms.eventtestproducer.oppgave.Oppgave
import no.nav.tms.eventtestproducer.tokenx.EventhandlerTokendings

class DoneEventService(
        private val handlerConsumer: HandlerConsumer,
        private val eventhandlerTokendings: EventhandlerTokendings,
        private val doneProducer: DoneProducer
) {

    suspend fun markAllBrukernotifikasjonerAsDone(innloggetBruker: InnloggetBruker) {
        val exchangedToken = eventhandlerTokendings.exchangeToken(innloggetBruker)
        val activeBeskjedEvents = handlerConsumer.getActiveEvents<Beskjed>(Eventtype.BESKJED, exchangedToken)
        val activeOppgaveEvents = handlerConsumer.getActiveEvents<Oppgave>(Eventtype.OPPGAVE, exchangedToken)
        val activeInnboksEvents = handlerConsumer.getActiveEvents<Innboks>(Eventtype.INNBOKS, exchangedToken)
        val alleBrukernotifikasjoner = activeBeskjedEvents + activeOppgaveEvents + activeInnboksEvents

        alleBrukernotifikasjoner.forEach { brukernotifikasjon ->
            doneProducer.produceDoneEventForSpecifiedEvent(innloggetBruker, brukernotifikasjon)
        }
    }
}
