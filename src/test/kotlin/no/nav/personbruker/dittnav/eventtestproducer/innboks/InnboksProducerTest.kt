package no.nav.personbruker.dittnav.eventtestproducer.innboks

import de.huxhorn.sulky.ulid.ULID
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.Innboks
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventtestproducer.common.createKeyForEvent
import no.nav.personbruker.dittnav.eventtestproducer.common.kafka.KafkaProducerWrapper
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class InnboksProducerTest {
    private val fodselsnummer = "12345678910"
    private val eventId = ULID().nextULID()
    private val systembruker = "x-dittNAV"
    private val link = "https://dummy.nav.no"
    private val tekst = "dummyTekst"
    private val grupperingsid = "dummyGrupperingsid"
    private val innlogetBruker = InnloggetBrukerObjectMother.createInnloggetBruker(fodselsnummer)
    private val innboksKafkaProducer = mockk<KafkaProducerWrapper<Innboks>>()
    private val innboksProducer = InnboksProducer(innboksKafkaProducer, systembruker)

    @Test
    fun `should create innboks-event`() {
        runBlocking {
            val innboksDto = ProduceInnboksDto(tekst, link, grupperingsid)
            val innboksKafkaEvent = innboksProducer.createInnboksForIdent(innlogetBruker, innboksDto)
            innboksKafkaEvent.getLink() `should be equal to` link
            innboksKafkaEvent.getTekst() `should be equal to` tekst
            innboksKafkaEvent.getGrupperingsId() `should be equal to` grupperingsid
        }
    }

    @Test
    fun `should create innboks-key`() {
        runBlocking {
            val nokkel = createKeyForEvent(eventId, systembruker)
            nokkel.getEventId() `should be equal to` eventId
            nokkel.getSystembruker() `should be equal to` systembruker
        }
    }

}
