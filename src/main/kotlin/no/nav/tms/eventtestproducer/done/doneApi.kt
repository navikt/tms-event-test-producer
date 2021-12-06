package no.nav.tms.eventtestproducer.done

import io.ktor.routing.Route
import io.ktor.routing.post
import no.nav.tms.eventtestproducer.config.innloggetBruker
import no.nav.tms.eventtestproducer.config.respond

fun Route.doneApi(doneEventService: DoneEventService) {

    post("/produce/done/all") {
        respond {
            doneEventService.markAllBrukernotifikasjonerAsDone(innloggetBruker)
            "Done-eventer er produsert for alle for brukeren: $innloggetBruker sine brukernotifikasjoner."
        }
    }

}
