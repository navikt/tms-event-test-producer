package no.nav.tms.eventtestproducer.utkast

import no.nav.tms.utkast.builder.UtkastJsonBuilder
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.*

class MultiUtkastProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {

    private val defaltLink = "https://www.dev.nav.no"

    private val log = LoggerFactory.getLogger(MultiUtkastProducer::class.java)

    fun sendCreate(ident: String, multiUtkast: MultiUtkast) {

        (0..multiUtkast.count).forEach {
            val number = it + multiUtkast.idOffset

            val utkastId = utkastId(number)
            val tittel = "Utkast created [$number]"

            val json = UtkastJsonBuilder()
                .withUtkastId(utkastId)
                .withTittel(tittel)
                .withIdent(ident)
                .withLink(defaltLink)
                .apply {
                    multiUtkast.languages.forEach { la ->
                        withTittelI18n("$la - $tittel", Locale(la))
                    }
                }
                .create()

            val producerRecord = ProducerRecord(topicName, utkastId, json)
            kafkaProducer.send(producerRecord)
        }

        log.info("Sendte ${multiUtkast.count} utkast-create meldinger til kafka.")
    }

    fun sendUpdate(ident: String, multiUtkast: MultiUtkast) {
        (0..multiUtkast.count).forEach {
            val number = it + multiUtkast.idOffset

            val utkastId = utkastId(number)
            val tittel = "Utkast updated [$number]"

            val json = UtkastJsonBuilder()
                .withUtkastId(utkastId)
                .withIdent(ident)
                .apply {
                    if (multiUtkast.updateTittel) {
                        withTittel(tittel)
                    }
                    multiUtkast.languages.forEach { la ->
                        withTittelI18n("$la - $tittel", Locale(la))
                    }
                }
                .update()

            val producerRecord = ProducerRecord(topicName, utkastId, json)
            kafkaProducer.send(producerRecord)
        }

        log.info("Sendte ${multiUtkast.count} utkast-update meldinger til kafka.")
    }

    fun sendDelete(multiUtkast: MultiUtkast) {
        (0..multiUtkast.count).forEach {
            val number = it + multiUtkast.idOffset

            val utkastId = utkastId(number)

            val json = UtkastJsonBuilder()
                .withUtkastId(utkastId)
                .delete()

            val producerRecord = ProducerRecord(topicName, utkastId, json)
            kafkaProducer.send(producerRecord)
        }

        log.info("Sendte ${multiUtkast.count} utkast-delete meldinger til kafka.")
    }

    private fun utkastId(number: Int): String = "00000000-0000-0000-0000-${String.format("%012x", number)}"
}
