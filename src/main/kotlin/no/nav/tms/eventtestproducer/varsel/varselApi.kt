package no.nav.tms.eventtestproducer.varsel

import io.ktor.server.routing.*
import no.nav.tms.eventtestproducer.util.innloggetBruker
import no.nav.tms.eventtestproducer.util.respondForParameterType

fun Route.varselApi(varselProducer: VarselProducer) {

    post("/produce/varsel") {
        this.respondForParameterType<ProduceVarselDto> { varselDto ->
            varselProducer.produceOpprettVarselForUser(innloggetBruker, varselDto)
            "Et opprett-${varselDto.type}-event for brukeren: $innloggetBruker har blitt lagt på kafka."
        }
    }

}
