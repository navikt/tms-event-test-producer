package no.nav.tms.eventtestproducer.statuskort

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tms.eventtestproducer.setup.innloggetBruker

fun Route.statuskortApi(statuskortProducer: StatuskortProducer) {

    post("/statuskort") {
        val request = call.receive<StatuskortRequest>().also(::validateRequest)

        val type = StatuskortRequestType.parse(request.type!!)

        val statuskortId = when (type) {
            StatuskortRequestType.Opprett -> {
                statuskortProducer.opprettStatuskort(innloggetBruker, request.innhold!!, request.tjeneste!!)
            }
            StatuskortRequestType.Oppdater -> {
                statuskortProducer.oppdaterStatuskort(request.statuskortId!!, request.innhold!!)
            }
            StatuskortRequestType.Inaktiver -> {
                statuskortProducer.inaktiverStatuskort(request.statuskortId!!)
            }
        }

        call.response.header("statuskortId", statuskortId)

        call.respondText("Et $type-Statuskort event med id [$statuskortId] er blitt lagt på kafka.")
    }
}

private fun validateRequest(request: StatuskortRequest) {
    if (request.type == null) {
        throw IllegalArgumentException("Må spesifisere requesttype")
    }

    val requestType = StatuskortRequestType.parse(request.type)

    when (requestType) {
        StatuskortRequestType.Opprett -> {
            requireNotNull(request.innhold) { "Må spesifisere innhold for Opprett-request" }
            requireNotNull(request.tjeneste) { "Må spesifisere tjeneste for Opprett-request" }
        }
        StatuskortRequestType.Oppdater -> {
            requireNotNull(request.statuskortId) { "Må spesifisere statuskortId for Oppdater-request" }
            requireNotNull(request.innhold) { "Må spesifisere innhold for Oppdater-request" }
        }
        StatuskortRequestType.Inaktiver -> {
            requireNotNull(request.statuskortId) { "Må spesifisere statuskortId for Inaktiver-request" }
        }
    }
}

enum class StatuskortRequestType {
    Opprett, Oppdater, Inaktiver;

    companion object {
        fun parse(string: String): StatuskortRequestType {
            return entries
                .firstOrNull { it.name.equals(string, ignoreCase = true) }
                ?: throw IllegalArgumentException("$string er ikke en gyldig request-type.")
        }
    }
}
