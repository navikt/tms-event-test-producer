package no.nav.tms.eventtestproducer.statusoppdatering

import io.ktor.routing.Route
import io.ktor.routing.post
import no.nav.tms.eventtestproducer.config.innloggetBruker
import no.nav.tms.eventtestproducer.config.respondForParameterType

fun Route.statusoppdateringApi(statusoppdateringProducer: StatusoppdateringProducer) {

    post("/produce/statusoppdatering") {
        this.respondForParameterType<ProduceStatusoppdateringDto> { statusoppdateringDto ->
            statusoppdateringProducer.produceStatusoppdateringEventForIdent(innloggetBruker, statusoppdateringDto)
            "Et statusoppdatering-event for brukeren: $innloggetBruker har blitt lagt på kafka."
        }
    }

}
