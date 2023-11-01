package no.nav.tms.eventtestproducer.setup

import no.nav.brukernotifikasjon.schemas.builders.domain.Eventtype
import no.nav.brukernotifikasjon.schemas.input.*
import no.nav.tms.eventtestproducer.beskjed.BeskjedProducer
import no.nav.tms.eventtestproducer.innboks.InnboksProducer
import no.nav.tms.eventtestproducer.microfrontend.MicrofrontendProducer
import no.nav.tms.eventtestproducer.oppgave.OppgaveProducer
import no.nav.tms.eventtestproducer.setup.Kafka.initializeRapidKafkaProducer
import no.nav.tms.eventtestproducer.setup.Kafka.producerProps
import no.nav.tms.eventtestproducer.utkast.MultiUtkastProducer
import no.nav.tms.eventtestproducer.utkast.UtkastRapidProducer
import no.nav.tms.eventtestproducer.varsel.VarselProducer
import org.apache.kafka.clients.producer.KafkaProducer

class ApplicationContext {

    val environment = Environment()

    val kafkaProducerBeskjed = KafkaProducerWrapper(environment.beskjedInputTopicName, KafkaProducer<NokkelInput, BeskjedInput>(producerProps(environment, Eventtype.BESKJED)))
    val beskjedProducer = BeskjedProducer(environment, kafkaProducerBeskjed)

    val kafkaProducerOppgave = KafkaProducerWrapper(environment.oppgaveInputTopicName, KafkaProducer<NokkelInput, OppgaveInput>(producerProps(environment, Eventtype.OPPGAVE)))
    val oppgaveProducer = OppgaveProducer(environment, kafkaProducerOppgave)

    val kafkaProducerInnboks = KafkaProducerWrapper(environment.innboksInputTopicName, KafkaProducer<NokkelInput, InnboksInput>(producerProps(environment, Eventtype.INNBOKS)))
    val innboksProducer = InnboksProducer(environment, kafkaProducerInnboks)

    val kafkaRapidProducer = initializeRapidKafkaProducer(environment)
    val utkastRapidProducer = UtkastRapidProducer(kafkaRapidProducer, environment.utkastTopicName)
    val utkastMultiProducer = MultiUtkastProducer(kafkaRapidProducer, environment.utkastTopicName)

    val microfrontendKafkaProducer = initializeRapidKafkaProducer(environment)

    val microfrontendProducer = MicrofrontendProducer(microfrontendKafkaProducer)

    val varselKafkaProducer = initializeRapidKafkaProducer(environment)
    val varselProducer = VarselProducer(varselKafkaProducer, environment.brukervarselTopicName)
}
