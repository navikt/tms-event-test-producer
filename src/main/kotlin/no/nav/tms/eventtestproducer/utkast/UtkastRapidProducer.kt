package no.nav.tms.eventtestproducer.utkast

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UtkastRapidProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {
    val objectMapper = ObjectMapper()

    val log: Logger = LoggerFactory.getLogger(Producer::class.java)

    fun createUtkast(user: IdportenUser, utkastCreate: UtkastCreate) {
        val objectNode = objectMapper.createObjectNode()
        objectNode.put("@event_name", "created")
        objectNode.put("ident", user.ident)
        objectNode.put("tittel", utkastCreate.tittel)
        objectNode.put("link", utkastCreate.link)
        val producerRecord = ProducerRecord(topicName, utkastCreate.eventId, objectNode.toString())
        kafkaProducer.send(producerRecord)
        log.info("Produsert utkast-created på rapid med eventid ${utkastCreate.eventId}")
    }

    fun updateUtkast(user: IdportenUser, utkastUpdate: UtkastUpdate) {
        val objectNode = objectMapper.createObjectNode()
        objectNode.put("@event_name", "updated")
        objectNode.put("ident", user.ident)
        utkastUpdate.tittel?.let { objectNode.put("tittel", it) }
        utkastUpdate.link?.let { objectNode.put("link", it) }
        val producerRecord = ProducerRecord(topicName, utkastUpdate.eventId, objectNode.toString())
        kafkaProducer.send(producerRecord)
        log.info("Produsert utkast-updated på rapid med eventid ${utkastUpdate.eventId}")
    }

    fun deleteUtkast(user: IdportenUser, eventId: String) {
        val objectNode = objectMapper.createObjectNode()
        objectNode.put("@event_name", "deleted")
        objectNode.put("ident", user.ident)
        val producerRecord = ProducerRecord(topicName, eventId, objectNode.toString())
        kafkaProducer.send(producerRecord)
        log.info("Produsert utkast-deleted på rapid med eventid $eventId")
    }
}
