package no.nav.tms.eventtestproducer.varsel

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tms.eventtestproducer.setup.innloggetBruker

fun Route.varselApi(varselProducer: VarselProducer) {

    post("/produce/varsel") {
        val varselRequest = call.receive<ProduceVarselRequest>()

        varselProducer.produceOpprettVarselForUser(innloggetBruker, varselRequest)
        call.respondText(
            text = "Et opprett-varsel event med type [${varselRequest.type}] er blitt lagt på kafka.",
        )
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
