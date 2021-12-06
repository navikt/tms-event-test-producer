package no.nav.personbruker.dittnav.eventtestproducer.config

import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json
import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.brukernotifikasjon.schemas.input.*
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.BeskjedProducer
import no.nav.personbruker.dittnav.eventtestproducer.common.HandlerConsumer
import no.nav.personbruker.dittnav.eventtestproducer.common.HttpClientBuilder
import no.nav.personbruker.dittnav.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventtestproducer.done.DoneEventService
import no.nav.personbruker.dittnav.eventtestproducer.done.DoneProducer
import no.nav.personbruker.dittnav.eventtestproducer.innboks.InnboksProducer
import no.nav.personbruker.dittnav.eventtestproducer.oppgave.OppgaveProducer
import no.nav.personbruker.dittnav.eventtestproducer.statusoppdatering.StatusoppdateringProducer
import no.nav.personbruker.dittnav.eventtestproducer.tokenx.EventhandlerTokendings
import no.nav.personbruker.dittnav.eventtestproducer.ytelsestesting.TestDataService
import no.nav.tms.token.support.tokendings.exchange.TokendingsServiceBuilder
import org.apache.kafka.clients.producer.KafkaProducer
import javax.ws.rs.client.Entity.json

class ApplicationContext {

    val environment = Environment()

    val httpClient = HttpClientBuilder.build(KotlinxSerializer(Json))

    val handlerConsumer = HandlerConsumer(httpClient, environment.eventHandlerURL)

    val tokendingsService = TokendingsServiceBuilder.buildTokendingsService()
    val eventhandlerTokendings = EventhandlerTokendings(tokendingsService, environment.eventhandlerClientId)

    val kafkaProducerBeskjed = KafkaProducerWrapper(environment.beskjedInputTopicName, KafkaProducer<NokkelInput, BeskjedInput>(Kafka.producerProps(environment, Eventtype.BESKJED)))
    val beskjedProducer = BeskjedProducer(environment, kafkaProducerBeskjed)

    val kafkaProducerOppgave = KafkaProducerWrapper(environment.oppgaveInputTopicName, KafkaProducer<NokkelInput, OppgaveInput>(Kafka.producerProps(environment, Eventtype.OPPGAVE)))
    val oppgaveProducer = OppgaveProducer(environment, kafkaProducerOppgave)

    val kafkaProducerInnboks = KafkaProducerWrapper(environment.innboksInputTopicName, KafkaProducer<NokkelInput, InnboksInput>(Kafka.producerProps(environment, Eventtype.INNBOKS)))
    val innboksProducer = InnboksProducer(environment, kafkaProducerInnboks)

    val kafkaProducerDone = KafkaProducerWrapper(environment.doneInputTopicName, KafkaProducer<NokkelInput, DoneInput>(Kafka.producerProps(environment, Eventtype.DONE)))
    val doneProducer = DoneProducer(environment, kafkaProducerDone)

    val kafkaProducerStatusoppdatering = KafkaProducerWrapper(environment.statusoppdateringInputTopicName, KafkaProducer<NokkelInput, StatusoppdateringInput>(Kafka.producerProps(environment, Eventtype.STATUSOPPDATERING)))
    val statusoppdateringProducer = StatusoppdateringProducer(environment, kafkaProducerStatusoppdatering)

    val doneEventService = DoneEventService(handlerConsumer, eventhandlerTokendings, doneProducer)
    val testDataService = TestDataService(doneProducer, beskjedProducer, oppgaveProducer, innboksProducer, statusoppdateringProducer)

}
