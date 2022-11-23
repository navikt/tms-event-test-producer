package no.nav.tms.eventtestproducer.utkast

import de.huxhorn.sulky.ulid.ULID
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.tms.eventtestproducer.config.innloggetBruker
import java.util.*

fun Route.utkastApi(producer: UtkastRapidProducer) {
    get("/uuid/generate") {
        call.respondText(UUID.randomUUID().toString())
    }

    get("/ulid/generate")  {
        call.respondText(ULID().nextULID())
    }

    post("/utkast/create") {
        val utkastCreate = call.receive<UtkastCreate>()

        producer.createUtkast(innloggetBruker, utkastCreate)

        call.respond(HttpStatusCode.OK)
    }

    post("/utkast/update") {
        val utkastUpdate = call.receive<UtkastUpdate>()

        if (utkastUpdate.tittel == null && utkastUpdate.link == null){
            call.respondText("Må sette minst én av tittel og link", status = HttpStatusCode.BadRequest)
        } else {
            producer.updateUtkast(innloggetBruker, utkastUpdate)

            call.respond(HttpStatusCode.OK)
        }
    }

    post("/utkast/delete") {
        val utkastDelete = call.receive<UtkastDelete>()

        producer.deleteUtkast(innloggetBruker, utkastDelete.utkastId)

        call.respond(HttpStatusCode.OK)
    }
}
