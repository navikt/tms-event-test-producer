package no.nav.personbruker.dittnav.eventtestproducer.beskjed

import no.nav.brukernotifikasjon.schemas.Beskjed
import no.nav.brukernotifikasjon.schemas.Nokkel
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventtestproducer.common.createKeyForEvent
import no.nav.personbruker.dittnav.eventtestproducer.config.Environment
import no.nav.personbruker.dittnav.eventtestproducer.config.Kafka
import no.nav.personbruker.dittnav.eventtestproducer.config.Kafka.beskjedTopicName
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit

object BeskjedProducer {

    private val log = LoggerFactory.getLogger(BeskjedProducer::class.java)
    val env = Environment()

    fun produceBeskjedEventForIdent(innloggetBruker: InnloggetBruker, dto: ProduceBeskjedDto) {
        KafkaProducer<Nokkel, Beskjed>(Kafka.producerProps(env)).use { producer ->
            val key = createKeyForEvent(env.systemUserName)
            val value = createBeskjedForIdent(innloggetBruker, dto)
            producer.send(ProducerRecord(beskjedTopicName, key, value))
        }
        log.info("Har produsert et beskjeds-event for for brukeren: $innloggetBruker")
    }

    private fun createBeskjedForIdent(innloggetBruker: InnloggetBruker, dto: ProduceBeskjedDto): Beskjed {
        val nowInMs = Instant.now().toEpochMilli()
        val weekFromNowInMs = Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()
        val build = Beskjed.newBuilder()
                .setFodselsnummer(innloggetBruker.ident)
                .setGrupperingsId("100$nowInMs")
                .setLink(dto.link)
                .setTekst(dto.tekst)
                .setTidspunkt(nowInMs)
                .setSynligFremTil(weekFromNowInMs)
                .setSikkerhetsnivaa(innloggetBruker.innloggingsnivaa)
        return build.build()
    }

}
