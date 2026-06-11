package no.nav.tms.eventtestproducer.varsel

import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tms.eventtestproducer.setup.innloggetBruker
import java.time.ZonedDateTime
import kotlin.math.min

fun Route.varselApi(varselProducer: VarselProducer) {

    post("/produce/varsel") {
        val varselRequest = call.receive<ProduceVarselRequest>()

        val antall = antallParam

        if (antall == null || antall < 2) {

            varselProducer.produceOpprettVarselForUser(innloggetBruker, varselRequest)

            call.respondText(
                text = "Et opprett-varsel event med type [${varselRequest.type}] er blitt lagt på kafka.",
            )
        } else {
            val clampedAntall = min(antall, 50_000) // Hindre bestilling av ekstreme mengder varsler

            repeat(clampedAntall) {
                varselProducer.produceOpprettVarselForUser(innloggetBruker, varselRequest)
            }

            call.respondText(
                text = "$antall opprett-varsel eventer med type [${varselRequest.type}] er blitt lagt på kafka.",
            )
        }
    }

    post("/produce/{type}") {
        val varselRequest = call.receive<ProduceVarselRequest>()

        varselRequest.type = typeParam

        varselProducer.produceOpprettVarselForUser(innloggetBruker, varselRequest)
        call.respondText(
            text = "Et opprett-varsel event med type [$typeParam] er blitt lagt på kafka.",
            contentType = ContentType.Text.Plain
        )
    }
}

private val RoutingContext.typeParam get() = call.pathParameters["type"]!!
private val RoutingContext.antallParam get(): Int? {
    return try {
        call.queryParameters["antall"]?.toInt()
    } catch (e: Exception) {
        null
    }
}
