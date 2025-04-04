package no.nav.tms.eventtestproducer.innboks

import no.nav.brukernotifikasjon.schemas.builders.InnboksInputBuilder
import no.nav.brukernotifikasjon.schemas.builders.NokkelInputBuilder
import no.nav.brukernotifikasjon.schemas.input.InnboksInput
import no.nav.brukernotifikasjon.schemas.input.NokkelInput
import no.nav.tms.eventtestproducer.util.getPrefererteKanaler
import no.nav.tms.eventtestproducer.setup.Environment
import no.nav.tms.eventtestproducer.setup.KafkaProducerWrapper
import no.nav.tms.token.support.idporten.sidecar.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class InnboksProducer(private val environment: Environment, private val innboksKafkaProducer: KafkaProducerWrapper<NokkelInput, InnboksInput>) {

    private val log = LoggerFactory.getLogger(InnboksProducer::class.java)

    fun produceInnboksEventForIdent(innloggetBruker: IdportenUser, dto: ProduceInnboksDto) {
        try {
            val key = createNokkelInput(innloggetBruker.ident, dto)
            val event = createInnboksInput(toLegacyLoginLevel(innloggetBruker.levelOfAssurance), dto)
            sendEventToKafka(key, event)
        } catch (e: Exception) {
            log.error("Det skjedde en feil ved produsering av et event for brukeren $innloggetBruker", e)
        }
    }

    fun sendEventToKafka(key: NokkelInput, event: InnboksInput) {
        innboksKafkaProducer.sendEvent(key, event)
    }

    fun createNokkelInput(ident: String, dto: ProduceInnboksDto): NokkelInput {
        return NokkelInputBuilder()
            .withEventId(UUID.randomUUID().toString())
            .withGrupperingsId(dto.grupperingsid)
            .withFodselsnummer(ident)
            .withNamespace(environment.namespace)
            .withAppnavn(environment.appnavn)
            .build()
    }

    fun createInnboksInput(loginLevel: Int, dto: ProduceInnboksDto): InnboksInput {
        val nowInMs = LocalDateTime.now(ZoneOffset.UTC)
        val builder = InnboksInputBuilder()
            .withTidspunkt(nowInMs)
            .withTekst(dto.tekst)
            .withLink(URI.create(dto.link).toURL())
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
