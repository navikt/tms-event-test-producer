package no.nav.tms.eventtestproducer.varsel

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tms.eventtestproducer.setup.KafkaProducerWrapper
import no.nav.tms.token.support.idporten.sidecar.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.varsel.action.*
import no.nav.tms.varsel.builder.VarselActionBuilder
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class VarselProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {

    private val log = KotlinLogging.logger {}

    fun produceBeskjedEventForIdent(innloggetBruker: IdportenUser, dto: ProduceVarselDto) {
        try {
            val varselId = UUID.randomUUID().toString()

            sendEvent(
                key = varselId,
                event = opprettVarselEvent(innloggetBruker, varselId, dto)
            )
        } catch (e: Exception) {
            log.error(e) { "Det skjedde en feil ved produsering av et event for brukeren $innloggetBruker" }
        }
    }

    private fun sendEvent(key: String, event: String) {
        kafkaProducer.send(ProducerRecord(topicName, key, event))
    }

    private fun opprettVarselEvent(innloggetBruker: IdportenUser, eventId: String, dto: ProduceVarselDto): String {
        return VarselActionBuilder.opprett {
            type = parseEnum<Varseltype>(dto.type)
            varselId = eventId
            sensitivitet = mapSensitivity(innloggetBruker.levelOfAssurance)
            ident = innloggetBruker.ident
            tekst = Tekst(
                spraakkode = dto.spraak,
                tekst = dto.tekst,
                default = true
            )
            link = dto.link
            aktivFremTil = dto.aktivFremTil
            eksternVarsling = if (dto.eksternVarsling) {
                EksternVarslingBestilling(
                    prefererteKanaler = dto.prefererteKanaler.map { parseEnum<EksternKanal>(it) },
                    smsVarslingstekst = dto.smsVarslingstekst,
                    epostVarslingstittel = dto.epostVarslingstittel,
                    epostVarslingstekst = dto.epostVarslingstekst
                )
            } else {
                null
            }
        }
    }

    private inline fun <reified T: Enum<T>> parseEnum(string: String): T {
        return enumValues<T>()
            .firstOrNull { it.name.lowercase() == string.lowercase() }
            ?: throw IllegalArgumentException("$string er ikke en gyldig ${T::class.simpleName}")
    }

    private fun mapSensitivity(loa: LevelOfAssurance): Sensitivitet {
       return when(loa) {
           LevelOfAssurance.SUBSTANTIAL -> Sensitivitet.Substantial
           LevelOfAssurance.HIGH -> Sensitivitet.High
       }
    }
}
