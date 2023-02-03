package no.nav.tms.eventtestproducer.beskjed

import io.ktor.server.routing.*
import no.nav.tms.eventtestproducer.util.innloggetBruker
import no.nav.tms.eventtestproducer.util.respondForParameterType

fun Route.beskjedApi(beskjedProducer: BeskjedProducer) {

    post("/produce/beskjed") {
        this.respondForParameterType<ProduceBeskjedDto> { beskjedDto ->
            beskjedProducer.produceBeskjedEventForIdent(innloggetBruker, beskjedDto)
            "Et beskjed-event for brukeren: $innloggetBruker har blitt lagt på kafka."
        }
    }

}
