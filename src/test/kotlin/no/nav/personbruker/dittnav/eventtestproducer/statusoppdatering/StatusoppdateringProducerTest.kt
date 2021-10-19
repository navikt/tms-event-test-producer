package no.nav.personbruker.dittnav.eventtestproducer.statusoppdatering

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.brukernotifikasjon.schemas.input.StatusoppdateringInput
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBrukerObjectMother
import no.nav.personbruker.dittnav.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventtestproducer.common.util.createPropertiesForTestEnvironment
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be empty`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.Test

class StatusoppdateringProducerTest {
    private val fodselsnummer = "12345678910"
    private val statusGlobal = "FERDIG"
    private val statusInternal = "dummyStatusInternal"
    private val sikkerhetsnivaa = 4
    private val sakstema = "dummySakstema"
    private val link = "https://dummy.nav.no"
    private val grupperingsid = "dummyGrupperingsid"
    private val innlogetBruker = InnloggetBrukerObjectMother.createInnloggetBruker(fodselsnummer)
    private val environment = createPropertiesForTestEnvironment()
    private val statusoppdateringKafkaProducer = mockk<KafkaProducerWrapper<NokkelInput, StatusoppdateringInput>>()
    private val statusoppdateringProducer = StatusoppdateringProducer(environment, statusoppdateringKafkaProducer)

    @Test
    fun `should create statusoppdatering-event`() {
        runBlocking {
            val statusoppdateringDto = ProduceStatusoppdateringDto(link, statusGlobal, statusInternal, sakstema, grupperingsid)
            val statusoppdateringKafkaEvent = statusoppdateringProducer.createStatusoppdateringInput(innlogetBruker, statusoppdateringDto)
            statusoppdateringKafkaEvent.getTidspunkt().`should not be null`()
            statusoppdateringKafkaEvent.getLink() `should be equal to` link
            statusoppdateringKafkaEvent.getSikkerhetsnivaa() `should be equal to` sikkerhetsnivaa
            statusoppdateringKafkaEvent.getStatusGlobal() `should be equal to` statusGlobal
            statusoppdateringKafkaEvent.getStatusIntern() `should be equal to` statusInternal
            statusoppdateringKafkaEvent.getSakstema() `should be equal to` sakstema
        }
    }

    @Test
    fun `should create statusoppdatering-key`() {
        runBlocking {
            val statusoppdateringDto = ProduceStatusoppdateringDto(link, statusGlobal, statusInternal, sakstema, grupperingsid)
            val nokkel = statusoppdateringProducer.createNokkelInput(innlogetBruker, statusoppdateringDto)
            nokkel.getEventId().`should not be empty`()
            nokkel.getGrupperingsId() `should be equal to` grupperingsid
            nokkel.getFodselsnummer() `should be equal to` fodselsnummer
            nokkel.getNamespace() `should be equal to` environment.namespace
            nokkel.getAppnavn() `should be equal to` environment.appnavn
        }
    }
}
