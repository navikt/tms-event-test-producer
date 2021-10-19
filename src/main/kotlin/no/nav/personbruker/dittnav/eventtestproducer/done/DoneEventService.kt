package no.nav.personbruker.dittnav.eventtestproducer.done

import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.Beskjed
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.getAktivBeskjedByFodselsnummer
import no.nav.personbruker.dittnav.eventtestproducer.common.HandlerConsumer
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventtestproducer.innboks.getAktivInnboksByFodselsnummer
import no.nav.personbruker.dittnav.eventtestproducer.oppgave.getAktivOppgaveByFodselsnummer
import no.nav.personbruker.dittnav.eventtestproducer.tokenx.EventhandlerTokendings

class DoneEventService(
        private val handlerConsumer: HandlerConsumer,
        private val eventhandlerTokendings: EventhandlerTokendings,
        private val doneProducer: DoneProducer
) {

    suspend fun markAllBrukernotifikasjonerAsDone(innloggetBruker: InnloggetBruker) {
        val exchangedToken = eventhandlerTokendings.exchangeToken(innloggetBruker)
        val activeBeskjedEvents = handlerConsumer.getActiveEvents<Beskjed>(Eventtype.BESKJED, exchangedToken)
        
        val beskjed = database.dbQuery { getAktivBeskjedByFodselsnummer(innloggetBruker) }
        val oppgaver = database.dbQuery { getAktivOppgaveByFodselsnummer(innloggetBruker) }
        val innboks = database.dbQuery { getAktivInnboksByFodselsnummer(innloggetBruker) }
        val alleBrukernotifikasjoner = beskjed + oppgaver + innboks

        alleBrukernotifikasjoner.forEach { brukernotifikasjon ->
            doneProducer.produceDoneEventForSpecifiedEvent(innloggetBruker, brukernotifikasjon)
        }
    }

}
