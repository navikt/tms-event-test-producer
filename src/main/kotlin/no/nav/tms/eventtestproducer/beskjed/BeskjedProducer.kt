package no.nav.tms.eventtestproducer.beskjed

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import no.nav.brukernotifikasjon.schemas.builders.BeskjedInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.tms.eventtestproducer.common.InnloggetBruker
import no.nav.tms.eventtestproducer.common.getPrefererteKanaler
import no.nav.tms.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.tms.eventtestproducer.config.Environment
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class BeskjedProducer(private val environment: Environment, private val beskjedKafkaProducer: KafkaProducerWrapper<NokkelInput, BeskjedInput>) {

    private val log = LoggerFactory.getLogger(BeskjedProducer::class.java)

    fun produceBeskjedEventForIdent(innloggetBruker: InnloggetBruker, dto: ProduceBeskjedDto) {
        try {
            val key = createNokkelInput(innloggetBruker, dto)
            val event = createBeskjedInput(innloggetBruker, dto)
            sendEventToKafka(key, event)
        } catch (e: Exception) {
            log.error("Det skjedde en feil ved produsering av et event for brukeren $innloggetBruker", e)
        }
    }

    fun sendEventToKafka(key: NokkelInput, event: BeskjedInput) {
        beskjedKafkaProducer.sendEvent(key, event)
    }

    fun createNokkelInput(innloggetBruker: InnloggetBruker, dto: ProduceBeskjedDto): NokkelInput {
        return NokkelInputBuilder()
            .withEventId(UUID.randomUUID().toString())
            .withGrupperingsId(dto.grupperingsid)
            .withFodselsnummer(innloggetBruker.ident)
            .withNamespace(environment.namespace)
            .withAppnavn(environment.appnavn)
            .build()
    }

    fun createBeskjedInput(innloggetBruker: InnloggetBruker, dto: ProduceBeskjedDto): BeskjedInput {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val builder = BeskjedInputBuilder()
                .withTidspunkt(now)
                .withTekst(dto.tekst)
                .withSikkerhetsnivaa(innloggetBruker.innloggingsnivaa)
                .withSynligFremTil(dto.synligFremTil?.toLocalDateTime(TimeZone.UTC)?.toJavaLocalDateTime())
                .withEksternVarsling(dto.eksternVarsling)
                .withEpostVarslingstekst(dto.epostVarslingstekst)
                .withEpostVarslingstittel(dto.epostVarslingstittel)
                .withSmsVarslingstekst(dto.smsVarslingstekst)
                .withPrefererteKanaler(*getPrefererteKanaler(dto.prefererteKanaler).toTypedArray())
        if(!dto.link.isNullOrBlank()) {
            builder.withLink(URL(dto.link))
        }
        return builder.build()
    }
}
