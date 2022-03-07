package no.nav.tms.eventtestproducer.oppgave

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import no.nav.brukernotifikasjon.schemas.builders.domain.PreferertKanal
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.brukernotifikasjon.schemas.input.OppgaveInput
import no.nav.tms.eventtestproducer.common.InnloggetBrukerObjectMother
import no.nav.tms.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.tms.eventtestproducer.common.util.createPropertiesForTestEnvironment
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test

class OppgaveProducerTest {

    private val fodselsnummer = "12345678910"
    private val link = "https://dummy.nav.no"
    private val tekst = "dummyTekst"
    private val grupperingsid = "dummyGrupperingsid"
    private val sikkerhetsnivaa = 4
    private val eksternVarsling = true
    private val prefererteKanaler = listOf(PreferertKanal.SMS.toString(), PreferertKanal.EPOST.toString())
    private val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker(fodselsnummer)
    private val synligFremTil = Clock.System.now().plus(7, DateTimeUnit.DAY, TimeZone.UTC)
    private val epostVarslingstekst = "<p>Du har fått en ny oppgave på Ditt NAV</p>"
    private val epostVarslingstittel = "Oppgave"
    private val smsVarslingstekst = "Du har fått en ny oppgave på Ditt NAV"
    private val environment = createPropertiesForTestEnvironment()
    private val oppgaveKafkaProducer = mockk<KafkaProducerWrapper<NokkelInput, OppgaveInput>>()
    private val oppgaveProducer = OppgaveProducer(environment, oppgaveKafkaProducer)

    @Test
    fun `should create oppgave-event`() {
        runBlocking {
            val oppgaveDto = ProduceOppgaveDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, synligFremTil, epostVarslingstekst, epostVarslingstittel, smsVarslingstekst)
            val oppgaveKafkaEvent = oppgaveProducer.createOppgaveInput(innloggetBruker, oppgaveDto)
            oppgaveKafkaEvent.getTidspunkt().`should not be null`()
            oppgaveKafkaEvent.getLink() `should be equal to` link
            oppgaveKafkaEvent.getTekst() `should be equal to` tekst
            oppgaveKafkaEvent.getSikkerhetsnivaa() `should be equal to` sikkerhetsnivaa
            oppgaveKafkaEvent.getEksternVarsling() `should be equal to` true
            oppgaveKafkaEvent.getPrefererteKanaler() `should be equal to` prefererteKanaler
            oppgaveKafkaEvent.getSynligFremTil() `should be equal to` synligFremTil.toEpochMilliseconds()
            oppgaveKafkaEvent.getEpostVarslingstekst() `should be equal to` epostVarslingstekst
            oppgaveKafkaEvent.getEpostVarslingstittel() `should be equal to` epostVarslingstittel
            oppgaveKafkaEvent.getSmsVarslingstekst() `should be equal to` smsVarslingstekst
        }
    }

    @Test
    fun `should create oppgave-key`() {
        runBlocking {
            val oppgaveDto = ProduceOppgaveDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler)
            val nokkel = oppgaveProducer.createNokkelInput(innloggetBruker, oppgaveDto)
            nokkel.getEventId().`should not be empty`()
            nokkel.getGrupperingsId() `should be equal to` grupperingsid
            nokkel.getFodselsnummer() `should be equal to` fodselsnummer
            nokkel.getNamespace() `should be equal to` environment.namespace
            nokkel.getAppnavn() `should be equal to` environment.appnavn
        }
    }

    @Test
    fun `should allow no value for prefererte kanaler`() {
        val oppgaveDto = ProduceOppgaveDto(tekst, link, grupperingsid, eksternVarsling)
        val oppgaveKafkaEvent = oppgaveProducer.createOppgaveInput(innloggetBruker, oppgaveDto)
        oppgaveKafkaEvent.getPrefererteKanaler().`should be empty`()
    }


    @Test
    fun `should allow no synligFremTil value`() {
        val oppgaveDto = ProduceOppgaveDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, synligFremTil = null)
        val oppgaveKafkaEvent = oppgaveProducer.createOppgaveInput(innloggetBruker, oppgaveDto)
        oppgaveKafkaEvent.getSynligFremTil() `should be equal to` null
    }

    @Test
    fun `should allow no epostVarslingstekst value`() {
        val oppgaveDto = ProduceOppgaveDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, epostVarslingstekst = null)
        val oppgaveKafkaEvent = oppgaveProducer.createOppgaveInput(innloggetBruker, oppgaveDto)
        oppgaveKafkaEvent.getEpostVarslingstekst() `should be equal to` null
    }

    @Test
    fun `should allow no epostVarslingstittel value`() {
        val oppgaveDto = ProduceOppgaveDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, epostVarslingstittel = null)
        val oppgaveKafkaEvent = oppgaveProducer.createOppgaveInput(innloggetBruker, oppgaveDto)
        oppgaveKafkaEvent.getEpostVarslingstittel() `should be equal to` null
    }

    @Test
    fun `should allow no smsVarslingstekst value`() {
        val oppgaveDto = ProduceOppgaveDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, smsVarslingstekst = null)
        val beskjedKafkaEvent = oppgaveProducer.createOppgaveInput(innloggetBruker, oppgaveDto)
        beskjedKafkaEvent.getSmsVarslingstekst() `should be equal to` null
    }
}
