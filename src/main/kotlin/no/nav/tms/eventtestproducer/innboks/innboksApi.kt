package no.nav.tms.eventtestproducer.innboks

import io.ktor.routing.Route
import io.ktor.routing.post
import no.nav.tms.eventtestproducer.config.innloggetBruker
import no.nav.tms.eventtestproducer.config.respondForParameterType

fun Route.innboksApi(innboksProducer: InnboksProducer) {

    post("/produce/innboks") {
        this.respondForParameterType<ProduceInnboksDto> { innboksDto ->
            innboksProducer.produceInnboksEventForIdent(innloggetBruker, innboksDto)
            "Et innboks-event for for brukeren: $innloggetBruker har blitt lagt på kafka."
        }
    }

}
