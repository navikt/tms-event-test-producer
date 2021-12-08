package no.nav.tms.eventtestproducer.oppgave

import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.OppgaveInputBuilder
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.brukernotifikasjon.schemas.input.OppgaveInput
import no.nav.tms.eventtestproducer.common.InnloggetBruker
import no.nav.tms.eventtestproducer.common.getPrefererteKanaler
import no.nav.tms.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.tms.eventtestproducer.config.Environment
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*

class OppgaveProducer(private val environment: Environment, private val oppgaveKafkaProducer: KafkaProducerWrapper<NokkelInput, OppgaveInput>) {

    private val log = LoggerFactory.getLogger(OppgaveProducer::class.java)

    fun produceOppgaveEventForIdent(innloggetBruker: InnloggetBruker, dto: ProduceOppgaveDto) {
        try {
            val key = createNokkelInput(innloggetBruker, dto)
            val event = createOppgaveInput(innloggetBruker, dto)
            sendEventToKafka(key, event)
        } catch (e: Exception) {
            log.error("Det skjedde en feil ved produsering av et event for brukeren $innloggetBruker", e)
        }
    }

    fun sendEventToKafka(key: NokkelInput, event: OppgaveInput) {
        oppgaveKafkaProducer.sendEvent(key, event)
    }

    fun createNokkelInput(innloggetBruker: InnloggetBruker, dto: ProduceOppgaveDto): NokkelInput {
        return NokkelInputBuilder()
            .withEventId(UUID.randomUUID().toString())
            .withGrupperingsId(dto.grupperingsid)
            .withFodselsnummer(innloggetBruker.ident)
            .withNamespace(environment.namespace)
            .withAppnavn(environment.appnavn)
            .build()
    }

    fun createOppgaveInput(innloggetBruker: InnloggetBruker, dto: ProduceOppgaveDto): OppgaveInput {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val weekFromNow = now.plus(7, ChronoUnit.DAYS)
        val builder = OppgaveInputBuilder()
            .withTidspunkt(now)
            .withSynligFremTil(weekFromNow)
            .withTekst(dto.tekst)
            .withLink(URL(dto.link))
            .withSikkerhetsnivaa(innloggetBruker.innloggingsnivaa)
            .withEksternVarsling(dto.eksternVarsling)
            .withPrefererteKanaler(*getPrefererteKanaler(dto.prefererteKanaler).toTypedArray())
        return builder.build()
    }
}
