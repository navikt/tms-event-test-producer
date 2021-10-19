package no.nav.personbruker.dittnav.eventtestproducer.ytelsestesting

import kotlinx.coroutines.delay
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.BeskjedProducer
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.ProduceBeskjedDto
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBruker
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
    val dummySystembruker = "dittnav-event-test-producer"

    suspend fun produserBeskjeder(produceDone: Boolean, yTestDto: YTestDto) {
        log.info("Produserer ${yTestDto.antallEventer} beskjeder")
        val start = Instant.now()
        val innloggetBruker = InnloggetBruker(yTestDto.ident, 4, "dummyToken")
        for (i in 1..yTestDto.antallEventer) {
            val dto = ProduceBeskjedDto(tekst = "Beskjedtekst $i", link = "https://beskjed-$i", grupperingsid = "grupperingsid-$i", eksternVarsling = yTestDto.eksternVarsling)
            val nokkelInput = beskjedProducer.createNokkelInput(innloggetBruker, dto)
            val beskjedInput = beskjedProducer.createBeskjedInput(innloggetBruker, dto)
            beskjedProducer.sendEventToKafka(nokkelInput, beskjedInput)
            if(produceDone) {
                val doneEvent = doneProducer.createDoneInput()
                doneProducer.sendEventToKafka(nokkelInput, doneEvent)
            }
            if (isShouldTakeASmallBreakAndLogProgress(i, yTestDto.antallEventer)) {
                log.info("Har produsert beskjed nummer $i tar en liten pause")
                delay(1000)
            }
        }
        beregnBruktTid(start)
    }

    suspend fun produserOppgaver(produceDone: Boolean, yTestDto: YTestDto) {
        log.info("Produserer ${yTestDto.antallEventer} oppgaver")
        val start = Instant.now()
        val innloggetBruker = InnloggetBruker(yTestDto.ident, 4, "dummyToken")
        for (i in 1..yTestDto.antallEventer) {
            val dto = ProduceOppgaveDto(tekst = "Oppgavetekst $i", link = "https://oppgave-$i", grupperingsid = "grupperingsid-$i", eksternVarsling = yTestDto.eksternVarsling)
            val nokkelInput = oppgaveProducer.createNokkelInput(innloggetBruker, dto)
            val oppgaveInput = oppgaveProducer.createOppgaveInput(innloggetBruker, dto)
            oppgaveProducer.sendEventToKafka(nokkelInput, oppgaveInput)
            if(produceDone) {
                val doneEvent = doneProducer.createDoneInput()
                doneProducer.sendEventToKafka(nokkelInput, doneEvent)
            }
            if (isShouldTakeASmallBreakAndLogProgress(i, yTestDto.antallEventer)) {
                log.info("Har produsert oppgave nummer $i tar en liten pause")
                delay(1000)
            }
        }
        beregnBruktTid(start)
    }

    suspend fun produserInnboks(produceDone: Boolean, yTestDto: YTestDto) {
        log.info("Produserer ${yTestDto.antallEventer} innboks-eventer")
        val start = Instant.now()
        val innloggetBruker = InnloggetBruker(yTestDto.ident, 4, "dummyToken")
        for (i in 1..yTestDto.antallEventer) {
            val dto = ProduceInnboksDto("Innbokstekst $i", "https://innboks-$i", "grupperingsid-$i")
            val nokkelInput = innboksProducer.createNokkelInput(innloggetBruker, dto)
            val innboksInput = innboksProducer.createInnboksInput(innloggetBruker, dto)
            innboksProducer.sendEventToKafka(nokkelInput, innboksInput)
            if(produceDone) {
                val doneEvent = doneProducer.createDoneInput()
                doneProducer.sendEventToKafka(nokkelInput, doneEvent)
            }
            if (isShouldTakeASmallBreakAndLogProgress(i, yTestDto.antallEventer)) {
                log.info("Har produsert innboks-event nummer $i tar en liten pause")
                delay(1000)
            }
        }
        beregnBruktTid(start)
    }

    suspend fun produserStatusoppdateringer(yTestDto: YTestDto) {
        log.info("Produserer ${yTestDto.antallEventer} Statusoppdatering-eventer")
        val start = Instant.now()
        val innloggetBruker = InnloggetBruker(yTestDto.ident, 4, "dummyToken")
        for (i in 1..yTestDto.antallEventer) {
            val dto = ProduceStatusoppdateringDto("https://dummyLink_$i", "SENDT", "dummyStatusIntern_$i", "dummySakstema_$i", "grupperingsid-$i")
            val nokkelInput = statusoppdateringProducer.createNokkelInput(innloggetBruker, dto)
            val statusoppdateringInput = statusoppdateringProducer.createStatusoppdateringInput(innloggetBruker, dto)
            statusoppdateringProducer.sendEventToKafka(nokkelInput, statusoppdateringInput)
            if (isShouldTakeASmallBreakAndLogProgress(i, yTestDto.antallEventer)) {
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

    private fun isShouldTakeASmallBreakAndLogProgress(i: Int, antallEventer: Int) = i % (antallEventer / 10) == 0

}
