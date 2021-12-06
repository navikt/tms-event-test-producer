package no.nav.tms.eventtestproducer.config

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineContext

suspend inline fun PipelineContext<Unit, ApplicationCall>.respond(handler: () -> String) {
    val message = handler.invoke()
    call.respondText(text = message, contentType = ContentType.Text.Plain)
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respondForParameterType(handler:(T) -> String) {
    val postParametersDto: T = call.receive()
    val message = handler.invoke(postParametersDto)
    call.respondText(text = message, contentType = ContentType.Text.Plain)
}
