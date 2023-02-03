package no.nav.tms.eventtestproducer.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUserFactory

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondForParameterType(handler:(T) -> String) {
    val postParametersDto: T = call.receive()
    val message = handler.invoke(postParametersDto)
    call.respondText(text = message, contentType = ContentType.Text.Plain)
}

val PipelineContext<Unit, ApplicationCall>.innloggetBruker: IdportenUser
    get() = IdportenUserFactory.createIdportenUser(call)
