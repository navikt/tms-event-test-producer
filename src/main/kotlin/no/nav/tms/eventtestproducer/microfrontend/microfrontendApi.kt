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
import org.intellij.lang.annotations.Language
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val knowIds = listOf("mk1", "mk2", "mk3", "mk4")

private val PipelineContext<Unit, ApplicationCall>.microfrontendId: String
    get() = call.parameters["microfrontendId"]?.also { id ->
        if (knowIds.none { it == id }) throw IllegalArgumentException("Ukjent microfrontendId")
    } ?: throw IllegalArgumentException("microfrontendId mangler i path")

fun Route.microfrontedApi(microfrontendProducer: MicrofrontendProducer) {

    route("microfrontend/{microfrontendId}") {

        post("enable") {
            val transform = when(call.request.queryParameters["version"]){
                "1" -> ::v1Enable
                "2" -> ::v2Enable
                else -> ::v3Enable
            }

            microfrontendProducer.produceEnable(innloggetBruker.ident, microfrontendId, transform)
            call.respond(HttpStatusCode.OK)
        }

        post("disable") {
            val transform = when(call.request.queryParameters["version"]){
                "1" -> ::v1Disable
                "2" -> ::v2Disable
                else -> ::v3Disable
            }

            microfrontendProducer.produceDisable(innloggetBruker.ident, microfrontendId, transform)
            call.respond(HttpStatusCode.OK)
        }
    }
}

class MicrofrontendProducer(
    private val kafkaProducer: Producer<String, String>,

    ) {
    private val log: Logger = LoggerFactory.getLogger(Producer::class.java)
    private val topicName: String = "min-side.aapen-microfrontend-v1"

    fun produceEnable(ident: String, microfrontendId: String, transform: (String, String) -> String) {

        kafkaProducer.send(ProducerRecord(topicName, "$microfrontendId$ident", transform(ident, microfrontendId)))
        log.info("Produsert microfrontend-enable på topic med microfrontendId$ident")
    }

    fun produceDisable(ident: String, microfrontendId: String, transform:(String,String)->String) {
        kafkaProducer.send(ProducerRecord(topicName, transform(ident, microfrontendId)))
        log.info("Produsert microfrontend-disable på topic med microfrontendId$ident")
    }

}


@Language("JSON")
fun v3Enable(ident: String, microfrontendId: String) = """
        {
        "@action":"enable",
        "ident": "$ident",
        "microfrontend_id":"$microfrontendId",
        "sensitivitet": "high",
        "@initiated_by":"minside-testproducer"
        }
    """.trimIndent()

@Language("JSON")
fun v2Enable(ident: String, microfrontendId: String) = """
        {
        "@action":"enable",
        "ident": "$ident",
        "microfrontend_id":"$microfrontendId",
        "sikkerhetsnivå": 4,
        "initiated_by":"minside-testproducer"
        }
    """.trimIndent()

fun v1Enable(ident: String, microfrontendId: String) = """
        {
        "@action":"enable",
        "ident": "$ident",
        "microfrontend_id":"$microfrontendId"
        }
    """.trimIndent()

@Language("JSON")
fun v3Disable(ident: String, microfrontendId: String) = """
        {
        "@action":"disable",
        "ident": "$ident",
        "microfrontend_id":"$microfrontendId",
        "@initiated_by":"minside-testproducer"
        }
    """.trimIndent()

@Language("JSON")
fun v2Disable(ident: String, microfrontendId: String) = """
        {
        "@action":"disable",
        "ident": "$ident",
        "microfrontend_id":"$microfrontendId",
        "initiated_by":"minside-testproducer"
        }
    """.trimIndent()

@Language("JSON")
fun v1Disable(ident: String, microfrontendId: String) = """
        {
        "@action":"disable",
        "ident": "$ident",
        "microfrontend_id":"$microfrontendId"
        }
    """.trimIndent()
