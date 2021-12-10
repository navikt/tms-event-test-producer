package no.nav.tms.eventtestproducer.beskjed

import io.ktor.routing.Route
import io.ktor.routing.post
import no.nav.tms.eventtestproducer.config.innloggetBruker
import no.nav.tms.eventtestproducer.config.respondForParameterType

fun Route.beskjedApi(beskjedProducer: BeskjedProducer) {

    post("/produce/beskjed") {
        this.respondForParameterType<ProduceBeskjedDto> { beskjedDto ->
            beskjedProducer.produceBeskjedEventForIdent(innloggetBruker, beskjedDto)
            "Et beskjed-event for brukeren: $innloggetBruker har blitt lagt på kafka."
        }
    }

}
