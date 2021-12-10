package no.nav.tms.eventtestproducer.innboks

import no.nav.brukernotifikasjon.schemas.builders.InnboksInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.input.InnboksInput
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

class InnboksProducer(private val environment: Environment, private val innboksKafkaProducer: KafkaProducerWrapper<NokkelInput, InnboksInput>) {

    private val log = LoggerFactory.getLogger(InnboksProducer::class.java)

    fun produceInnboksEventForIdent(innloggetBruker: InnloggetBruker, dto: ProduceInnboksDto) {
        try {
            val key = createNokkelInput(innloggetBruker, dto)
            val event = createInnboksInput(innloggetBruker, dto)
            sendEventToKafka(key, event)
        } catch (e: Exception) {
            log.error("Det skjedde en feil ved produsering av et event for brukeren $innloggetBruker", e)
        }
    }

    fun sendEventToKafka(key: NokkelInput, event: InnboksInput) {
        innboksKafkaProducer.sendEvent(key, event)
    }

    fun createNokkelInput(innloggetBruker: InnloggetBruker, dto: ProduceInnboksDto): NokkelInput {
        return NokkelInputBuilder()
            .withEventId(UUID.randomUUID().toString())
            .withGrupperingsId(dto.grupperingsid)
            .withFodselsnummer(innloggetBruker.ident)
            .withNamespace(environment.namespace)
            .withAppnavn(environment.appnavn)
            .build()
    }

    fun createInnboksInput(innloggetBruker: InnloggetBruker, dto: ProduceInnboksDto): InnboksInput {
        val nowInMs = LocalDateTime.now(ZoneOffset.UTC)
        val builder = InnboksInputBuilder()
            .withTidspunkt(nowInMs)
            .withTekst(dto.tekst)
            .withLink(URL(dto.link))
            .withSikkerhetsnivaa(innloggetBruker.innloggingsnivaa)
            .withEksternVarsling(dto.eksternVarsling)
            .withPrefererteKanaler(*getPrefererteKanaler(dto.prefererteKanaler).toTypedArray())
        return builder.build()
    }
}
