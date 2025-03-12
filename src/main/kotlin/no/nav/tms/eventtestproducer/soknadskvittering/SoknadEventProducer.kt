package no.nav.tms.eventtestproducer.soknadskvittering

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tms.soknadskvittering.builder.SoknadEventBuilder
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.time.ZonedDateTime
import java.util.*

class SoknadEventProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {

    private val log = KotlinLogging.logger {}

    fun innsendSoknad(innloggetBruker: IdportenUser, opprett: SoknadRequest.Innsend): String? = try {
        val soknadsId = UUID.randomUUID().toString()

        val opprettetEvent = innsendEvent(soknadsId, innloggetBruker.ident, opprett)

        sendEvent(
            key = soknadsId,
            event = opprettetEvent
        )

        soknadsId
    } catch (e: Exception) {
        log.error(e) { "Det skjedde en feil ved produsering av et soknadOpprettet-event for brukeren $innloggetBruker" }
        null
    }

    fun oppdaterSoknad(oppdater: SoknadRequest.Oppdater) {
        val opprettetEvent = oppdatertEvent(oppdater)

        sendEvent(
            key = oppdater.soknadsId,
            event = opprettetEvent
        )
    }

    fun ferdigstillSoknad(ferdigstill: SoknadRequest.Ferdigstill) {
        val opprettetEvent = ferdigstiltEvent(ferdigstill)

        sendEvent(
            key = ferdigstill.soknadsId,
            event = opprettetEvent
        )
    }

    fun ettersporrVedlegg(ettersporr: SoknadRequest.EttersporrVedlegg) {
        val etterspurtEvent = vedleggEtterspurtEvent(ettersporr)

        sendEvent(
            key = ettersporr.soknadsId,
            event = etterspurtEvent
        )
    }

    fun mottaVedlegg(motta: SoknadRequest.MottaVedlegg) {
        val mottattEvent = vedleggMottattEvent(motta)

        sendEvent(
            key = motta.soknadsId,
            event = mottattEvent
        )
    }


    private fun sendEvent(key: String, event: String) {
        kafkaProducer.send(ProducerRecord(topicName, key, event))
    }

    private fun innsendEvent(soknadsId: String, ident: String, opprett: SoknadRequest.Innsend) = SoknadEventBuilder.innsendt {
        this.soknadsId = soknadsId
        this.ident = ident
        tittel = opprett.tittel
        temakode = opprett.temakode
        skjemanummer = opprett.skjemanummer
        fristEttersending = opprett.fristEttersending
        linkSoknad = opprett.linkSoknad
        journalpostId = opprett.journalpostId
        opprett.mottatteVedlegg.forEach {
            mottattVedlegg {
                vedleggsId = it.vedleggsId
                tittel = it.tittel
                linkVedlegg = it.linkVedlegg
            }
        }
        opprett.etterspurteVedlegg.forEach {
            etterspurtVedlegg {
                vedleggsId = it.vedleggsId
                brukerErAvsender = it.brukerErAvsender
                tittel = it.tittel
                beskrivelse = it.beskrivelse
                linkEttersending = it.linkEttersending
            }
        }
        tidspunktMottatt = ZonedDateTime.now()
    }

    private fun oppdatertEvent(oppdater: SoknadRequest.Oppdater) = SoknadEventBuilder.oppdatert {
        soknadsId = oppdater.soknadsId
        fristEttersending = oppdater.fristEttersending
        linkSoknad = oppdater.linkSoknad
        journalpostId = oppdater.journalpostId
    }

    private fun ferdigstiltEvent(ferdigstill: SoknadRequest.Ferdigstill) = SoknadEventBuilder.ferdigstilt {
        soknadsId = ferdigstill.soknadsId
    }

    private fun vedleggEtterspurtEvent(ettersporrVedlegg: SoknadRequest.EttersporrVedlegg) =
        if (ettersporrVedlegg.brukerErAvsender) {
            SoknadEventBuilder.vedleggEtterspurtBruker {
                soknadsId = ettersporrVedlegg.soknadsId
                vedleggsId = ettersporrVedlegg.vedleggsId
                tittel = ettersporrVedlegg.tittel
                linkEttersending = ettersporrVedlegg.linkEttersending
                tidspunktEtterspurt = ZonedDateTime.now()
            }
        } else {
            SoknadEventBuilder.vedleggEtterspurtTredjepart {
                soknadsId = ettersporrVedlegg.soknadsId
                vedleggsId = ettersporrVedlegg.vedleggsId
                tittel = ettersporrVedlegg.tittel
                beskrivelse = ettersporrVedlegg.beskrivelse
                tidspunktEtterspurt = ZonedDateTime.now()
            }
        }

    private fun vedleggMottattEvent(mottaVedlegg: SoknadRequest.MottaVedlegg) = SoknadEventBuilder.vedleggMottatt {
        soknadsId = mottaVedlegg.soknadsId
        vedleggsId = mottaVedlegg.vedleggsId
        tittel = mottaVedlegg.tittel
        linkVedlegg = mottaVedlegg.linkVedlegg
        journalpostId = mottaVedlegg.journalpostId
        brukerErAvsender = mottaVedlegg.brukerErAvsender
        tidspunktMottatt = ZonedDateTime.now()
    }
}
