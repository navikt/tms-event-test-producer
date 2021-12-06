package no.nav.personbruker.dittnav.eventtestproducer.ytelsestesting

import kotlinx.coroutines.runBlocking
import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.brukernotifikasjon.schemas.input.*
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.BeskjedProducer
import no.nav.personbruker.dittnav.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventtestproducer.common.util.createPropertiesForTestEnvironment
import no.nav.personbruker.dittnav.eventtestproducer.config.Kafka
import no.nav.personbruker.dittnav.eventtestproducer.done.DoneProducer
import no.nav.personbruker.dittnav.eventtestproducer.innboks.InnboksProducer
import no.nav.personbruker.dittnav.eventtestproducer.oppgave.OppgaveProducer
import no.nav.personbruker.dittnav.eventtestproducer.statusoppdatering.StatusoppdateringProducer
import org.apache.kafka.clients.producer.KafkaProducer

internal class TestDataServiceTest {

    private val env = createPropertiesForTestEnvironment()

    private val kafkaProducerDone = KafkaProducerWrapper(env.doneInputTopicName, KafkaProducer<NokkelInput, DoneInput>(Kafka.producerProps(env, Eventtype.DONE)))
    private val doneProducer = DoneProducer(env, kafkaProducerDone)

    private val kafkaProducerBeskjed = KafkaProducerWrapper(env.beskjedInputTopicName, KafkaProducer<NokkelInput, BeskjedInput>(Kafka.producerProps(env, Eventtype.BESKJED)))
    private val beskjedProducer = BeskjedProducer(env, kafkaProducerBeskjed)

    private val kafkaProducerInnboks = KafkaProducerWrapper(env.innboksInputTopicName, KafkaProducer<NokkelInput, InnboksInput>(Kafka.producerProps(env, Eventtype.INNBOKS)))
    private val innboksProducer = InnboksProducer(env, kafkaProducerInnboks)

    private val kafkaProducerOppgave = KafkaProducerWrapper(env.oppgaveInputTopicName, KafkaProducer<NokkelInput, OppgaveInput>(Kafka.producerProps(env, Eventtype.OPPGAVE)))
    private val oppgaveProducer = OppgaveProducer(env, kafkaProducerOppgave)

    private val kafkaProducerStatusoppdatering = KafkaProducerWrapper(env.statusoppdateringInputTopicName, KafkaProducer<NokkelInput, StatusoppdateringInput>(Kafka.producerProps(env, Eventtype.STATUSOPPDATERING)))
    private val statusoppdateringProducer = StatusoppdateringProducer(env, kafkaProducerStatusoppdatering)

    //@Test
    fun produserTestcaseUtenEksternVarsling() {
        val testDataService = TestDataService(doneProducer, beskjedProducer, oppgaveProducer, innboksProducer, statusoppdateringProducer)
        runBlocking {
            testDataService.produserBeskjeder(produceDone = true, YTestDto())
            testDataService.produserOppgaver(produceDone = true, YTestDto())
            testDataService.produserInnboks(produceDone = true, YTestDto())
            testDataService.produserStatusoppdateringer(YTestDto())
        }
    }

    //@Test
    fun produserTestcaseMedEksternVarsling() {
        val testDataService = TestDataService(doneProducer, beskjedProducer, oppgaveProducer, innboksProducer, statusoppdateringProducer)
        runBlocking {
            testDataService.produserBeskjeder(produceDone = false, YTestDto(eksternVarsling = true))
            testDataService.produserOppgaver(produceDone = false, YTestDto(eksternVarsling = true))
            testDataService.produserInnboks(produceDone = false, YTestDto(eksternVarsling = true))
        }
    }

}
