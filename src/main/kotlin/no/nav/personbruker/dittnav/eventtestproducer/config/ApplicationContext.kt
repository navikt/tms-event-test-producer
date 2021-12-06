package no.nav.personbruker.dittnav.eventtestproducer.config

import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.brukernotifikasjon.schemas.input.*
import no.nav.personbruker.dittnav.eventtestproducer.beskjed.BeskjedProducer
import no.nav.personbruker.dittnav.eventtestproducer.common.database.Database
import no.nav.personbruker.dittnav.eventtestproducer.common.database.PostgresDatabase
import no.nav.personbruker.dittnav.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventtestproducer.done.DoneEventService
import no.nav.personbruker.dittnav.eventtestproducer.done.DoneProducer
import no.nav.personbruker.dittnav.eventtestproducer.innboks.InnboksProducer
import no.nav.personbruker.dittnav.eventtestproducer.oppgave.OppgaveProducer
import no.nav.personbruker.dittnav.eventtestproducer.statusoppdatering.StatusoppdateringProducer
import no.nav.personbruker.dittnav.eventtestproducer.ytelsestesting.TestDataService
import org.apache.kafka.clients.producer.KafkaProducer

class ApplicationContext {

    val environment = Environment()
    val database: Database = PostgresDatabase(environment)

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

    val doneEventService = DoneEventService(database, doneProducer)
    val testDataService = TestDataService(doneProducer, beskjedProducer, oppgaveProducer, innboksProducer, statusoppdateringProducer)

}
