package no.nav.personbruker.dittnav.eventtestproducer.done

import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.Beskjed
import no.nav.personbruker.dittnav.eventtestproducer.common.HandlerConsumer
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventtestproducer.innboks.Innboks
import no.nav.personbruker.dittnav.eventtestproducer.oppgave.Oppgave
import no.nav.personbruker.dittnav.eventtestproducer.tokenx.EventhandlerTokendings

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
