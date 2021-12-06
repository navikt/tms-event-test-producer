package no.nav.tms.eventtestproducer.oppgave

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
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
    private val environment = createPropertiesForTestEnvironment()
    private val oppgaveKafkaProducer = mockk<KafkaProducerWrapper<NokkelInput, OppgaveInput>>()
    private val oppgaveProducer = OppgaveProducer(environment, oppgaveKafkaProducer)

    @Test
    fun `should create oppgave-event`() {
        runBlocking {
            val oppgaveDto = ProduceOppgaveDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler)
            val oppgaveKafkaEvent = oppgaveProducer.createOppgaveInput(innloggetBruker, oppgaveDto)
            oppgaveKafkaEvent.getTidspunkt().`should not be null`()
            oppgaveKafkaEvent.getLink() `should be equal to` link
            oppgaveKafkaEvent.getTekst() `should be equal to` tekst
            oppgaveKafkaEvent.getSikkerhetsnivaa() `should be equal to` sikkerhetsnivaa
            oppgaveKafkaEvent.getEksternVarsling() `should be equal to` true
            oppgaveKafkaEvent.getPrefererteKanaler() `should be equal to` prefererteKanaler
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
}
