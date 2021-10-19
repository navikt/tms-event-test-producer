package no.nav.personbruker.dittnav.eventtestproducer.done

import no.nav.brukernotifikasjon.schemas.builders.DoneInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.input.DoneInput
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventtestproducer.common.database.Brukernotifikasjon
import no.nav.personbruker.dittnav.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventtestproducer.config.Environment
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneOffset

class DoneProducer(private val environment: Environment, private val doneKafkaProducer: KafkaProducerWrapper<NokkelInput, DoneInput>) {

    private val log = LoggerFactory.getLogger(DoneProducer::class.java)

    fun produceDoneEventForSpecifiedEvent(innloggetBruker: InnloggetBruker, eventThatsDone: Brukernotifikasjon) {
        try {
            val key = createNokkelInput(innloggetBruker, eventThatsDone.eventId)
            val doneEvent = createDoneInput()
            sendEventToKafka(key, doneEvent)
        } catch (e: Exception) {
            log.error("Det skjedde en feil ved produsering av et event for brukeren $innloggetBruker", e)
        }
    }

    fun sendEventToKafka(key: NokkelInput, event: DoneInput) {
        doneKafkaProducer.sendEvent(key, event)
    }

    fun createNokkelInput(innloggetBruker: InnloggetBruker, eventId: String): NokkelInput {
        return NokkelInputBuilder()
            .withEventId(eventId)
            .withFodselsnummer(innloggetBruker.ident)
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
