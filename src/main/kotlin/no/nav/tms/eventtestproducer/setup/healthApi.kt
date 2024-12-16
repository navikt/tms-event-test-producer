package no.nav.tms.eventtestproducer.setup

import io.ktor.http.ContentType
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthApi() {

    val pingJsonResponse = """{"ping": "pong"}"""

    get("/isAlive") {
        call.respondText(text = "ALIVE", contentType = ContentType.Text.Plain)
    }

    get("/isReady") {
        call.respondText(text = "READY", contentType = ContentType.Text.Plain)
    }

    get("/ping") {
        call.respondText(pingJsonResponse, ContentType.Application.Json)
    }

}
