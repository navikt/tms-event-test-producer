package no.nav.tms.eventtestproducer.utkast

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.utkast.builder.UtkastJsonBuilder
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class UtkastRapidProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {
    val objectMapper = ObjectMapper()

    val log: Logger = LoggerFactory.getLogger(Producer::class.java)

    fun createUtkast(user: IdportenUser, utkastCreate: UtkastCreate) {
        val json = UtkastJsonBuilder()
            .withIdent(user.ident)
            .withUtkastId(utkastCreate.utkastId)
            .withTittel(utkastCreate.tittel)
            .withLink(utkastCreate.link)
            .apply {
                utkastCreate.tittelI18n?.forEach { (spraak, tittel) ->
                    withTittelI18n(tittel, Locale(spraak))
                }
            }
            .create()

        val producerRecord = ProducerRecord(topicName, utkastCreate.utkastId, json)
        kafkaProducer.send(producerRecord)
        log.info("Produsert utkast-created på rapid med utkastId ${utkastCreate.utkastId}")
    }

    fun updateUtkast(user: IdportenUser, utkastUpdate: UtkastUpdate) {
        val json = UtkastJsonBuilder()
            .withIdent(user.ident)
            .withUtkastId(utkastUpdate.utkastId)
            .apply {
                if (utkastUpdate.tittel != null) {
                    withTittel(utkastUpdate.tittel)
                }

                if (utkastUpdate.link != null) {
                    withLink(utkastUpdate.link)
                }

                utkastUpdate.tittelI18n?.forEach { (spraak, tittel) ->
                    withTittelI18n(tittel, Locale(spraak))
                }
            }
            .update()

        val producerRecord = ProducerRecord(topicName, utkastUpdate.utkastId, json)
        kafkaProducer.send(producerRecord)
        log.info("Produsert utkast-updated på rapid med utkastId ${utkastUpdate.utkastId}")
    }

    fun deleteUtkast(utkastId: String) {
        val json = UtkastJsonBuilder()
            .withUtkastId(utkastId)
            .delete()

        val producerRecord = ProducerRecord(topicName, utkastId, json)
        kafkaProducer.send(producerRecord)
        log.info("Produsert utkast-deleted på rapid med utkastId $utkastId")
    }
}
