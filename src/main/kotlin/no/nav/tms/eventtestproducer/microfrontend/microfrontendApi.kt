package no.nav.tms.eventtestproducer.microfrontend

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext
import no.nav.tms.eventtestproducer.util.innloggetBruker
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val knowIds = listOf("mk1","mk2","mk3","mk4")

private val PipelineContext<Unit, ApplicationCall>.microfrontendId: String
    get() = call.parameters["microfrontendId"]?.also {id ->
       if (knowIds.none { it == id }) throw IllegalArgumentException("Ukjent microfrontendId")
    } ?: throw IllegalArgumentException("microfrontendId mangler i path")

fun Route.microfrontedApi(microfrontendProducer: MicrofrontendProducer) {

    route("microfrontend/{microfrontendId}") {

        post("enable") {
            microfrontendProducer.produceEnable(innloggetBruker.ident, microfrontendId)
            call.respond(HttpStatusCode.OK)
        }

        post("disable") {
            microfrontendProducer.produceDisable(innloggetBruker.ident, microfrontendId)
            call.respond(HttpStatusCode.OK)
        }
    }
}

class MicrofrontendProducer(
    private val kafkaProducer: Producer<String, String>,

    ) {
    private val log: Logger = LoggerFactory.getLogger(Producer::class.java)
    private val topicName: String = "min-side.aapen-microfrontend-v1"

    fun produceEnable(ident: String, microfrontendId: String) {
        kafkaProducer.send(ProducerRecord(topicName, v3Enable(ident, microfrontendId)))
        log.info("Produsert microfrontend-enable på topic med microfrontendId$ident")
    }

    fun produceDisable(ident: String, microfrontendId: String) {
        kafkaProducer.send(ProducerRecord(topicName, v3Disable(ident, microfrontendId)))
        log.info("Produsert microfrontend-disable på topic med microfrontendId$ident")
    }


    private fun v3Enable(ident: String, microfrontendId: String) = """
        "@action":"enable",
        "ident: "$ident",
        "microfrontend_id":"$microfrontendId"
        "sensitivitet": "high",
        "@initiated_by":"minside-testproducer"
    """.trimIndent()

    private fun v3Disable(ident: String, microfrontendId: String) = """
        "@action":"disable",
        "ident: "$ident",
        "microfrontend_id":"$microfrontendId"
        "@initiated_by":"minside-testproducer"
    """.trimIndent()


}