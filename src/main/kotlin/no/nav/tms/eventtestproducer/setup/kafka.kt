package no.nav.tms.eventtestproducer.setup

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

object Kafka {

    fun initializeRapidKafkaProducer(environment: Environment) = KafkaProducer<String, String>(
        Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.kafkaBrokers)
            put(
                ProducerConfig.CLIENT_ID_CONFIG,
                "tms-event-test-producer"
            )
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 40000)
            put(ProducerConfig.ACKS_CONFIG, "all")
            put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
            putAll(credentialProps(environment.securityVars))
        }
    )

    private fun credentialProps(securityVars: SecurityVars): Properties {
        return Properties().apply {
            put(SaslConfigs.SASL_MECHANISM, "PLAIN")
            put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL")
            put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "jks")
            put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12")
            put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, securityVars.kafkaTruststorePath)
            put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, securityVars.kafkaCredstorePassword)
            put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, securityVars.kafkaKeystorePath)
            put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, securityVars.kafkaCredstorePassword)
            put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, securityVars.kafkaCredstorePassword)
            put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "")
        }
    }
}

class KafkaProducerWrapper<K, V>(
    private val topicName: String,
    private val kafkaProducer: KafkaProducer<K, V>
) {

    private val log: Logger = LoggerFactory.getLogger(KafkaProducerWrapper::class.java)

    fun sendEvent(key: K, event: V) {
        ProducerRecord(topicName, key, event).let { producerRecord ->
            kafkaProducer.send(producerRecord)
        }
    }

    fun flushAndClose() {
        try {
            kafkaProducer.flush()
            kafkaProducer.close()
            log.info("Produsent for kafka-eventer er flushet og lukket.")
        } catch (e: Exception) {
            log.warn("Klarte ikke å flushe og lukke produsent. Det kan være eventer som ikke ble produsert.")
        }
    }
}
