package no.nav.tms.eventtestproducer.beskjed

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import no.nav.brukernotifikasjon.schemas.builders.domain.PreferertKanal
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.tms.eventtestproducer.common.InnloggetBrukerObjectMother
import no.nav.tms.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.tms.eventtestproducer.common.util.createPropertiesForTestEnvironment
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test

class BeskjedProducerTest {

    private val fodselsnummer = "12345678910"
    private val link = "https://dummy.nav.no"
    private val tekst = "dummyTekst"
    private val grupperingsid = "dummyGrupperingsid"
    private val sikkerhetsnivaa = 4
    private val eksternVarsling = true
    private val prefererteKanaler = listOf(PreferertKanal.SMS.toString(), PreferertKanal.EPOST.toString())
    private val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker(fodselsnummer)
    private val synligFremTil = Clock.System.now().plus(7, DateTimeUnit.DAY, TimeZone.UTC)
    private val epostVarslingstekst = "<p>Du har fått en ny beskjed på Ditt NAV</p>"
    private val epostVarslingstittel = "Beskjed"
    private val smsVarslingstekst = "Du har fått en ny beskjed på Ditt NAV"
    private val environment = createPropertiesForTestEnvironment()
    private val beskjedKafkaProducer = mockk<KafkaProducerWrapper<NokkelInput, BeskjedInput>>()
    private val beskjedProducer = BeskjedProducer(environment, beskjedKafkaProducer)

    @Test
    fun `should create beskjed-event`() {
        runBlocking {
            val beskjedDto = ProduceBeskjedDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, synligFremTil, epostVarslingstekst, epostVarslingstittel, smsVarslingstekst)
            val beskjedKafkaEvent = beskjedProducer.createBeskjedInput(innloggetBruker, beskjedDto)
            beskjedKafkaEvent.getTidspunkt().`should not be null`()
            beskjedKafkaEvent.getSynligFremTil().`should not be null`()
            beskjedKafkaEvent.getLink() `should be equal to` link
            beskjedKafkaEvent.getTekst() `should be equal to` tekst
            beskjedKafkaEvent.getSikkerhetsnivaa() `should be equal to` sikkerhetsnivaa
            beskjedKafkaEvent.getEksternVarsling() `should be equal to` true
            beskjedKafkaEvent.getPrefererteKanaler() `should be equal to` prefererteKanaler
            beskjedKafkaEvent.getSynligFremTil() `should be equal to` synligFremTil.toEpochMilliseconds()
            beskjedKafkaEvent.getEpostVarslingstekst() `should be equal to` epostVarslingstekst
            beskjedKafkaEvent.getEpostVarslingstittel() `should be equal to` epostVarslingstittel
            beskjedKafkaEvent.getSmsVarslingstekst() `should be equal to` smsVarslingstekst
        }
    }

    @Test
    fun `should allow no link value`() {
        val beskjedDto = ProduceBeskjedDto(tekst, null, grupperingsid, eksternVarsling)
        val beskjedKafkaEvent = beskjedProducer.createBeskjedInput(innloggetBruker, beskjedDto)
        beskjedKafkaEvent.getLink() `should be equal to` ""
    }

    @Test
    fun `should create beskjed-key`() {
        runBlocking {
            val beskjedDto = ProduceBeskjedDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler)
            val nokkel = beskjedProducer.createNokkelInput(innloggetBruker, beskjedDto)
            nokkel.getEventId().`should not be empty`()
            nokkel.getGrupperingsId() `should be equal to` grupperingsid
            nokkel.getFodselsnummer() `should be equal to` fodselsnummer
            nokkel.getNamespace() `should be equal to` environment.namespace
            nokkel.getAppnavn() `should be equal to` environment.appnavn
        }
    }

    @Test
    fun `should allow no value for prefererte kanaler`() {
        val beskjedDto = ProduceBeskjedDto(tekst, link, grupperingsid, eksternVarsling)
        val beskjedKafkaEvent = beskjedProducer.createBeskjedInput(innloggetBruker, beskjedDto)
        beskjedKafkaEvent.getPrefererteKanaler().`should be empty`()
    }

    @Test
    fun `should allow no synligFremTil value`() {
        val beskjedDto = ProduceBeskjedDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, synligFremTil = null)
        val beskjedKafkaEvent = beskjedProducer.createBeskjedInput(innloggetBruker, beskjedDto)
        beskjedKafkaEvent.getSynligFremTil() `should be equal to` null
    }

    @Test
    fun `should allow no epostVarslingstekst value`() {
        val beskjedDto = ProduceBeskjedDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, epostVarslingstekst = null)
        val beskjedKafkaEvent = beskjedProducer.createBeskjedInput(innloggetBruker, beskjedDto)
        beskjedKafkaEvent.getEpostVarslingstekst() `should be equal to` null
    }

    @Test
    fun `should allow no epostVarslingstittel value`() {
        val beskjedDto = ProduceBeskjedDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, epostVarslingstittel = null)
        val beskjedKafkaEvent = beskjedProducer.createBeskjedInput(innloggetBruker, beskjedDto)
        beskjedKafkaEvent.getEpostVarslingstittel() `should be equal to` null
    }

    @Test
    fun `should allow no smsVarslingstekst value`() {
        val beskjedDto = ProduceBeskjedDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler, smsVarslingstekst = null)
        val beskjedKafkaEvent = beskjedProducer.createBeskjedInput(innloggetBruker, beskjedDto)
        beskjedKafkaEvent.getSmsVarslingstekst() `should be equal to` null
    }
}
