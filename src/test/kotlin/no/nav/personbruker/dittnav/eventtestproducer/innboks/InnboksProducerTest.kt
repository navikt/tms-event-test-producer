package no.nav.personbruker.dittnav.eventtestproducer.innboks

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.builders.domain.PreferertKanal
import no.nav.brukernotifikasjon.schemas.input.InnboksInput
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventtestproducer.common.util.createPropertiesForTestEnvironment
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test
import java.util.*

class InnboksProducerTest {

    private val fodselsnummer = "12345678910"
    private val link = "https://dummy.nav.no"
    private val tekst = "dummyTekst"
    private val grupperingsid = "dummyGrupperingsid"
    private val sikkerhetsnivaa = 4
    private val eksternVarsling = true
    private val prefererteKanaler = listOf(PreferertKanal.SMS.toString(), PreferertKanal.EPOST.toString())
    private val innloggetBruker = InnloggetBrukerObjectMother.createInnloggetBruker(fodselsnummer)
    private val environment = createPropertiesForTestEnvironment()
    private val innboksKafkaProducer = mockk<KafkaProducerWrapper<NokkelInput, InnboksInput>>()
    private val innboksProducer = InnboksProducer(environment, innboksKafkaProducer)


    @Test
    fun `should create innboks-event`() {
        runBlocking {
            val innboksDto = ProduceInnboksDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler)
            val innboksKafkaEvent = innboksProducer.createInnboksInput(innloggetBruker, innboksDto)
            innboksKafkaEvent.getTidspunkt().`should not be null`()
            innboksKafkaEvent.getLink() `should be equal to` link
            innboksKafkaEvent.getTekst() `should be equal to` tekst
            innboksKafkaEvent.getSikkerhetsnivaa() `should be equal to` sikkerhetsnivaa
            innboksKafkaEvent.getEksternVarsling() `should be equal to` true
            innboksKafkaEvent.getPrefererteKanaler() `should be equal to` prefererteKanaler
        }
    }

    @Test
    fun `should create innboks-key`() {
        runBlocking {
            val innboksDto = ProduceInnboksDto(tekst, link, grupperingsid, eksternVarsling, prefererteKanaler)
            val nokkel = innboksProducer.createNokkelInput(innloggetBruker, innboksDto)
            nokkel.getEventId().`should not be empty`()
            nokkel.getGrupperingsId() `should be equal to` grupperingsid
            nokkel.getFodselsnummer() `should be equal to` fodselsnummer
            nokkel.getNamespace() `should be equal to` environment.namespace
            nokkel.getAppnavn() `should be equal to` environment.appnavn
        }
    }

    @Test
    fun `should allow no value for prefererte kanaler`() {
        val innboksDto = ProduceInnboksDto(tekst, link, grupperingsid, eksternVarsling)
        val innboksKafkaEvent = innboksProducer.createInnboksInput(innloggetBruker, innboksDto)
        innboksKafkaEvent.getPrefererteKanaler().`should be empty`()
    }

}
