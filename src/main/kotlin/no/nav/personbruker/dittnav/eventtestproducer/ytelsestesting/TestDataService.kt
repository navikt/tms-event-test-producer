package no.nav.personbruker.dittnav.eventtestproducer.ytelsestesting

import kotlinx.coroutines.delay
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.BeskjedProducer
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.ProduceBeskjedDto
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventtestproducer.common.createKeyForEvent
import no.nav.personbruker.dittnav.eventtestproducer.done.DoneProducer
import no.nav.personbruker.dittnav.eventtestproducer.innboks.InnboksProducer
import no.nav.personbruker.dittnav.eventtestproducer.innboks.ProduceInnboksDto
import no.nav.personbruker.dittnav.eventtestproducer.oppgave.OppgaveProducer
import no.nav.personbruker.dittnav.eventtestproducer.oppgave.ProduceOppgaveDto
import no.nav.personbruker.dittnav.eventtestproducer.statusoppdatering.ProduceStatusoppdateringDto
import no.nav.personbruker.dittnav.eventtestproducer.statusoppdatering.StatusoppdateringProducer
import org.slf4j.LoggerFactory
import java.time.Instant

class TestDataService(
        private val doneProducer: DoneProducer,
        private val beskjedProducer: BeskjedProducer,
        private val oppgaveProducer: OppgaveProducer,
        private val innboksProducer: InnboksProducer,
        private val statusoppdateringProducer: StatusoppdateringProducer
) {

    private val log = LoggerFactory.getLogger(TestDataService::class.java)
    private val bruker = InnloggetBruker("88888", 4, "dummyToken")
    private val dummySystembruker = "dittnav"
    private val antallEventer = 50000

    suspend fun produserBeskjederOgTilhorendeDoneEventer() {
        log.info("Produserer $antallEventer beskjeder")
        val start = Instant.now()
        for (i in 1..antallEventer) {
            val key = createKeyForEvent("b-$i", dummySystembruker)
            val dto = ProduceBeskjedDto("Beskjedtekst $i", "https://beskjed-$i")
            val beskjedEvent = beskjedProducer.createBeskjedForIdent(bruker, dto)
            val doneEvent = doneProducer.createDoneEvent(bruker)
            beskjedProducer.sendEventToKafka(key, beskjedEvent)
            doneProducer.sendEventToKafka(key, doneEvent)
            if (isShouldTakeASmallBreakAndLogProgress(i)) {
                log.info("Har produsert beskjed nummer $i tar en liten pause")
                delay(1000)
            }
        }
        beregnBruktTid(start)
    }

    suspend fun produserOppgaveOgTilhorendeDoneEventer() {
        log.info("Produserer $antallEventer oppgaver")
        val start = Instant.now()
        for (i in 1..antallEventer) {
            val key = createKeyForEvent("o-$i", dummySystembruker)
            val dto = ProduceOppgaveDto("Oppgavetekst $i", "https://oppgave-$i")
            val oppgaveEvent = oppgaveProducer.createOppgaveForIdent(bruker, dto)
            val doneEvent = doneProducer.createDoneEvent(bruker)
            oppgaveProducer.sendEventToKafka(key, oppgaveEvent)
            doneProducer.sendEventToKafka(key, doneEvent)
            if (isShouldTakeASmallBreakAndLogProgress(i)) {
                log.info("Har produsert oppgave nummer $i tar en liten pause")
                delay(1000)
            }
        }
        beregnBruktTid(start)
    }

    suspend fun produserInnboksOgTilhorendeDoneEventer() {
        log.info("Produserer $antallEventer innboks-eventer")
        val start = Instant.now()
        for (i in 1..antallEventer) {
            val key = createKeyForEvent("i-$i", dummySystembruker)
            val dto = ProduceInnboksDto("Innbokstekst $i", "https://innboks-$i")
            val innboksEvent = innboksProducer.createInnboksForIdent(bruker, dto)
            val doneEvent = doneProducer.createDoneEvent(bruker)
            innboksProducer.sendEventToKafka(key, innboksEvent)
            doneProducer.sendEventToKafka(key, doneEvent)
            if (isShouldTakeASmallBreakAndLogProgress(i)) {
                log.info("Har produsert innboks-event nummer $i tar en liten pause")
                delay(1000)
            }
        }
        beregnBruktTid(start)
    }

    suspend fun produserStatusoppdateringsEventer() {
        log.info("Produserer $antallEventer Statusoppdatering-eventer")
        val start = Instant.now()
        for (i in 1..antallEventer) {
            val key = createKeyForEvent("s-$i", dummySystembruker)
            val dto = ProduceStatusoppdateringDto("dummyLink_$i", "SENDT", "dummyStatusIntern_$i", "dummySakstema_$i")
            val statusoppdateringEvent = statusoppdateringProducer.createStatusoppdateringForIdent(bruker, dto)
            val doneEvent = doneProducer.createDoneEvent(bruker)
            statusoppdateringProducer.sendEventToKafka(key, statusoppdateringEvent)
            doneProducer.sendEventToKafka(key, doneEvent)
            if (isShouldTakeASmallBreakAndLogProgress(i)) {
                log.info("Har produsert Statusoppdatering-event nummer $i tar en liten pause")
                delay(1000)
            }
        }
        beregnBruktTid(start)
    }

    private fun beregnBruktTid(start: Instant) {
        val stop = Instant.now()
        val tidbrukt = stop.minusMillis(start.toEpochMilli()).toEpochMilli()
        val tidbruktISekunder = tidbrukt / 1000
        log.info("Gjenbrukt kafka-produsent med venting, produseringen tok: $tidbruktISekunder sekunder.\n")
    }

    private fun isShouldTakeASmallBreakAndLogProgress(i: Int) = i % (antallEventer / 10) == 0

}
