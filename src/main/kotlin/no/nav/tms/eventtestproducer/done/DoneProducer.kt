package no.nav.tms.eventtestproducer.done

import no.nav.brukernotifikasjon.schemas.builders.DoneInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.input.DoneInput
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.tms.eventtestproducer.common.InnloggetBruker
import no.nav.tms.eventtestproducer.common.BrukernotifikasjonNokkel
import no.nav.tms.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.tms.eventtestproducer.config.Environment
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneOffset

class DoneProducer(private val environment: Environment, private val doneKafkaProducer: KafkaProducerWrapper<NokkelInput, DoneInput>) {

    private val log = LoggerFactory.getLogger(DoneProducer::class.java)

    fun produceDoneEventForSpecifiedEvent(innloggetBruker: InnloggetBruker, eventThatsDone: BrukernotifikasjonNokkel) {
        try {
            val key = createNokkelInput(innloggetBruker, eventThatsDone.eventId, eventThatsDone.grupperingsId)
            val doneEvent = createDoneInput()
            sendEventToKafka(key, doneEvent)
        } catch (e: Exception) {
            log.error("Det skjedde en feil ved produsering av et event for brukeren $innloggetBruker", e)
        }
    }

    fun sendEventToKafka(key: NokkelInput, event: DoneInput) {
        doneKafkaProducer.sendEvent(key, event)
    }

    fun createNokkelInput(innloggetBruker: InnloggetBruker, eventId: String, grupperingsId: String): NokkelInput {
        return NokkelInputBuilder()
            .withEventId(eventId)
            .withFodselsnummer(innloggetBruker.ident)
            .withGrupperingsId(grupperingsId)
            .withNamespace(environment.namespace)
            .withAppnavn(environment.appnavn)
            .build()
    }

    fun createDoneInput(): DoneInput {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val builder = DoneInputBuilder()
                .withTidspunkt(now)
        return builder.build()
    }
}
