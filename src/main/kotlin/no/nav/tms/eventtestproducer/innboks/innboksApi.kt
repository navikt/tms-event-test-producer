package no.nav.tms.eventtestproducer.innboks

import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import no.nav.tms.eventtestproducer.util.innloggetBruker
import no.nav.tms.eventtestproducer.util.respondForParameterType

fun Route.innboksApi(innboksProducer: InnboksProducer) {

    post("/produce/innboks") {
        this.respondForParameterType<ProduceInnboksDto> { innboksDto ->
            innboksProducer.produceInnboksEventForIdent(innloggetBruker, innboksDto)
            "Et innboks-event for for brukeren: $innloggetBruker har blitt lagt på kafka."
        }
    }

}
