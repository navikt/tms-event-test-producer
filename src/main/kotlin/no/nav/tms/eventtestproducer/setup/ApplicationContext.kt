package no.nav.tms.eventtestproducer.setup

import no.nav.tms.eventtestproducer.microfrontend.MicrofrontendProducer
import no.nav.tms.eventtestproducer.statuskort.StatuskortProducer
import no.nav.tms.eventtestproducer.utkast.MultiUtkastProducer
import no.nav.tms.eventtestproducer.utkast.UtkastProducer
import no.nav.tms.eventtestproducer.varsel.VarselProducer
import no.nav.tms.kafka.producer.KafkaProducerBuilder

class ApplicationContext {
    val environment = Environment()

    val utkastRapidProducer = UtkastProducer(KafkaProducerBuilder.stringProducer(), environment.utkastTopicName)
    val utkastMultiProducer = MultiUtkastProducer(KafkaProducerBuilder.stringProducer(), environment.utkastTopicName)

    val microfrontendProducer = MicrofrontendProducer(KafkaProducerBuilder.stringProducer())

    val varselProducer = VarselProducer(KafkaProducerBuilder.stringProducer(), environment.brukervarselTopicName)
    val statuskortProducer = StatuskortProducer(
        kafkaProducer = KafkaProducerBuilder.stringProducer(),
        topicName = environment.statuskortTopicName
    )
}
