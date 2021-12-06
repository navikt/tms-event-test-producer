package no.nav.personbruker.dittnav.eventtestproducer.statusoppdatering

import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.StatusoppdateringInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.domain.StatusGlobal
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.brukernotifikasjon.schemas.input.StatusoppdateringInput
import no.nav.personbruker.dittnav.eventtestproducer.common.InnloggetBruker
import no.nav.personbruker.dittnav.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.personbruker.dittnav.eventtestproducer.config.Environment
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class StatusoppdateringProducer(private val environment: Environment, private val statusoppdateringKafkaProducer: KafkaProducerWrapper<NokkelInput, StatusoppdateringInput>) {

    private val log = LoggerFactory.getLogger(StatusoppdateringProducer::class.java)

    fun produceStatusoppdateringEventForIdent(innloggetBruker: InnloggetBruker, dto: ProduceStatusoppdateringDto) {
        try {
            val key = createNokkelInput(innloggetBruker, dto)
            val event = createStatusoppdateringInput(innloggetBruker, dto)
            sendEventToKafka(key, event)
        } catch (e: Exception) {
            log.error("Det skjedde en feil ved produsering av et event for brukeren $innloggetBruker", e)
        }
    }

    fun sendEventToKafka(key: NokkelInput, event: StatusoppdateringInput) {
        statusoppdateringKafkaProducer.sendEvent(key, event)
    }

    fun createNokkelInput(innloggetBruker: InnloggetBruker, dto: ProduceStatusoppdateringDto): NokkelInput {
        return NokkelInputBuilder()
            .withEventId(UUID.randomUUID().toString())
            .withGrupperingsId(dto.grupperingsid)
            .withFodselsnummer(innloggetBruker.ident)
            .withNamespace(environment.namespace)
            .withAppnavn(environment.appnavn)
            .build()
    }

    fun createStatusoppdateringInput(innloggetBruker: InnloggetBruker, dto: ProduceStatusoppdateringDto): StatusoppdateringInput {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val build = StatusoppdateringInputBuilder()
                .withTidspunkt(now)
                .withLink(URL(dto.link))
                .withSikkerhetsnivaa(innloggetBruker.innloggingsnivaa)
                .withStatusGlobal(StatusGlobal.valueOf(dto.statusGlobal))
                .withStatusIntern(dto.statusIntern)
                .withSakstema(dto.sakstema)
        return build.build()
    }
}
