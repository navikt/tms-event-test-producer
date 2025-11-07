package no.nav.tms.eventtestproducer.setup

import no.nav.tms.eventtestproducer.microfrontend.MicrofrontendProducer
import no.nav.tms.eventtestproducer.setup.Kafka.initializeRapidKafkaProducer
import no.nav.tms.eventtestproducer.utkast.MultiUtkastProducer
import no.nav.tms.eventtestproducer.utkast.UtkastRapidProducer
import no.nav.tms.eventtestproducer.varsel.VarselProducer

class ApplicationContext {

    val environment = Environment()

    val kafkaRapidProducer = initializeRapidKafkaProducer(environment)
    val utkastRapidProducer = UtkastRapidProducer(kafkaRapidProducer, environment.utkastTopicName)
    val utkastMultiProducer = MultiUtkastProducer(kafkaRapidProducer, environment.utkastTopicName)

    val microfrontendKafkaProducer = initializeRapidKafkaProducer(environment)

    val microfrontendProducer = MicrofrontendProducer(microfrontendKafkaProducer)

    val varselKafkaProducer = initializeRapidKafkaProducer(environment)
    val varselProducer = VarselProducer(varselKafkaProducer, environment.brukervarselTopicName)
}
