package no.nav.tms.eventtestproducer.soknadskvittering

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tms.eventtestproducer.util.innloggetBruker
import no.nav.tms.soknad.event.validation.SoknadskvitteringValidationException

fun Route.soknadApi(producer: SoknadEventProducer) {

    val log = KotlinLogging.logger {}

    post("/produce/soknad/opprett") {
        val opprettRequest: SoknadRequest.Innsend = call.receive()
        val soknadsId = producer.opprettSoknad(innloggetBruker, opprettRequest)

        if (soknadsId != null) {
            log.info { "Soknad-opprettet event med id [$soknadsId] er lagt på kafka" }
            call.respondText(soknadsId)
        } else {
            log.warn { "Feilaktig innhold i opprett-request" }
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/produce/soknad/oppdater") {
        try {
            val oppdaterRequest: SoknadRequest.Oppdater = call.receive()
            producer.oppdaterSoknad(oppdaterRequest)

            log.info { "Soknad-oppdatert event med id [${oppdaterRequest.soknadsId}] er lagt på kafka" }
        } catch (e: SoknadskvitteringValidationException) {
            log.warn(e) { "Feilaktig innhold i oppdater-request" }
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/produce/soknad/ferdigstill") {
        try {
            val ferdigstillRequest: SoknadRequest.Ferdigstill = call.receive()
            producer.ferdigstillSoknad(ferdigstillRequest)

            log.info { "Soknad-ferdigstilt event med id [${ferdigstillRequest.soknadsId}] er lagt på kafka" }
        } catch (e: SoknadskvitteringValidationException) {
            log.warn(e) { "Feilaktig innhold i ferdigstill-request" }
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/produce/soknad/vedlegg/motta") {
        try {
            val mottaRequest: SoknadRequest.MottaVedlegg = call.receive()
            producer.mottaVedlegg(mottaRequest)

            log.info { "Vedlegg-etterspurt event med id [${mottaRequest.soknadsId}] er lagt på kafka" }
        } catch (e: SoknadskvitteringValidationException) {
            log.warn(e) { "Feilaktig innhold i motta-vedlegg request" }
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/produce/soknad/vedlegg/ettersporr") {
        try {

            val ettersporrRequest: SoknadRequest.EttersporrVedlegg = call.receive()
            producer.ettersporrVedlegg(ettersporrRequest)

            log.info { "Vedlegg-mottatt event med id [${ettersporrRequest.soknadsId}] er lagt på kafka" }
        } catch (e: SoknadskvitteringValidationException) {
            log.warn(e) { "Feilaktig innhold i ettersporr-vedlegg request" }
            call.respond(HttpStatusCode.BadRequest)
        }
    }

}
