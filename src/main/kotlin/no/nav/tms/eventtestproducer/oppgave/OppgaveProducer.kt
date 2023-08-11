package no.nav.tms.eventtestproducer.oppgave

import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.OppgaveInputBuilder
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.brukernotifikasjon.schemas.input.OppgaveInput
import no.nav.tms.eventtestproducer.util.getPrefererteKanaler
import no.nav.tms.eventtestproducer.setup.Environment
import no.nav.tms.eventtestproducer.setup.KafkaProducerWrapper
import no.nav.tms.token.support.idporten.sidecar.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class OppgaveProducer(private val environment: Environment, private val oppgaveKafkaProducer: KafkaProducerWrapper<NokkelInput, OppgaveInput>) {

    private val log = LoggerFactory.getLogger(OppgaveProducer::class.java)

    fun produceOppgaveEventForIdent(innloggetBruker: IdportenUser, dto: ProduceOppgaveDto) {
        try {
            val key = createNokkelInput(innloggetBruker.ident, dto)
            val event = createOppgaveInput(toLegacyLoginLevel(innloggetBruker.levelOfAssurance), dto)
            sendEventToKafka(key, event)
        } catch (e: Exception) {
            log.error("Det skjedde en feil ved produsering av et event for brukeren $innloggetBruker", e)
        }
    }

    fun sendEventToKafka(key: NokkelInput, event: OppgaveInput) {
        oppgaveKafkaProducer.sendEvent(key, event)
    }

    fun createNokkelInput(ident: String, dto: ProduceOppgaveDto): NokkelInput {
        return NokkelInputBuilder()
            .withEventId(UUID.randomUUID().toString())
            .withGrupperingsId(dto.grupperingsid)
            .withFodselsnummer(ident)
            .withNamespace(environment.namespace)
            .withAppnavn(environment.appnavn)
            .build()
    }

    fun createOppgaveInput(loginLevel: Int, dto: ProduceOppgaveDto): OppgaveInput {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val builder = OppgaveInputBuilder()
            .withTidspunkt(now)
            .withSynligFremTil(dto.synligFremTil)
            .withTekst(dto.tekst)
            .withLink(URL(dto.link))
            .withSikkerhetsnivaa(loginLevel)
            .withEksternVarsling(dto.eksternVarsling)
            .withEpostVarslingstekst(dto.epostVarslingstekst)
            .withEpostVarslingstittel(dto.epostVarslingstittel)
            .withSmsVarslingstekst(dto.smsVarslingstekst)
            .withPrefererteKanaler(*getPrefererteKanaler(dto.prefererteKanaler).toTypedArray())
        return builder.build()
    }

    private fun toLegacyLoginLevel(loa: LevelOfAssurance) = when(loa) {
        LevelOfAssurance.SUBSTANTIAL -> 3
        LevelOfAssurance.HIGH -> 4
    }
}
