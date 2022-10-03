package no.nav.tms.eventtestproducer.oppgave

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.OppgaveInputBuilder
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.brukernotifikasjon.schemas.input.OppgaveInput
import no.nav.tms.eventtestproducer.common.getPrefererteKanaler
import no.nav.tms.eventtestproducer.common.kafka.KafkaProducerWrapper
import no.nav.tms.eventtestproducer.config.Environment
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*

class OppgaveProducer(private val environment: Environment, private val oppgaveKafkaProducer: KafkaProducerWrapper<NokkelInput, OppgaveInput>) {

    private val log = LoggerFactory.getLogger(OppgaveProducer::class.java)

    fun produceOppgaveEventForIdent(innloggetBruker: IdportenUser, dto: ProduceOppgaveDto) {
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

    fun createNokkelInput(innloggetBruker: IdportenUser, dto: ProduceOppgaveDto): NokkelInput {
        return NokkelInputBuilder()
            .withEventId(UUID.randomUUID().toString())
            .withGrupperingsId(dto.grupperingsid)
            .withFodselsnummer(innloggetBruker.ident)
            .withNamespace(environment.namespace)
            .withAppnavn(environment.appnavn)
            .build()
    }

    fun createOppgaveInput(innloggetBruker: IdportenUser, dto: ProduceOppgaveDto): OppgaveInput {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val builder = OppgaveInputBuilder()
            .withTidspunkt(now)
            .withSynligFremTil(dto.synligFremTil?.toLocalDateTime(TimeZone.UTC)?.toJavaLocalDateTime())
            .withTekst(dto.tekst)
            .withLink(URL(dto.link))
            .withSikkerhetsnivaa(innloggetBruker.loginLevel)
            .withEksternVarsling(dto.eksternVarsling)
            .withEpostVarslingstekst(dto.epostVarslingstekst)
            .withEpostVarslingstittel(dto.epostVarslingstittel)
            .withSmsVarslingstekst(dto.smsVarslingstekst)
            .withPrefererteKanaler(*getPrefererteKanaler(dto.prefererteKanaler).toTypedArray())
        return builder.build()
    }
}
