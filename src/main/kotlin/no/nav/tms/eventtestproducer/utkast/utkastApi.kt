package no.nav.tms.eventtestproducer.utkast

import de.huxhorn.sulky.ulid.ULID
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tms.common.util.config.StringEnvVar
import no.nav.tms.eventtestproducer.setup.innloggetBruker
import java.util.*

fun Route.utkastApi(producer: UtkastRapidProducer, multiProducer: MultiUtkastProducer) {
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

        producer.deleteUtkast(utkastDelete.utkastId)

        call.respond(HttpStatusCode.OK)
    }

    post("/utkast/custom") {
        val utkastId = call.request.headers["utkastId"]?: UUID.randomUUID().toString()

        val utkastAction = call.receiveText()

        producer.sendEvent(utkastId, utkastAction)

        call.respond(HttpStatusCode.OK)
    }

    if (StringEnvVar.getEnvVar("NAIS_CLUSTER_NAME") == "dev-gcp") {
        post("/utkast/multi/create") {
            val multiCreate = call.receive<MultiUtkast>()

            multiProducer.sendCreate(innloggetBruker.ident, multiCreate)

            call.respond(HttpStatusCode.OK)
        }

        post("/utkast/multi/update") {
            val multiUpdate = call.receive<MultiUtkast>()

            multiProducer.sendUpdate(innloggetBruker.ident, multiUpdate)

            call.respond(HttpStatusCode.OK)
        }

        post("/utkast/multi/delete") {
            val multiDelete = call.receive<MultiUtkast>()

            multiProducer.sendDelete(multiDelete)

            call.respond(HttpStatusCode.OK)
        }
    }

}
