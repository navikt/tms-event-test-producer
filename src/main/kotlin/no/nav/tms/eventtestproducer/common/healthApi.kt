package no.nav.tms.eventtestproducer.common

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

fun Routing.healthApi(collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry) {

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

    get("/metrics") {
        val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: emptySet()
        call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
            TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
        }
    }

}
