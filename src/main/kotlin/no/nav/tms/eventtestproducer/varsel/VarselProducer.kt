package no.nav.tms.eventtestproducer.varsel

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tms.token.support.idporten.sidecar.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.user.IdportenUser
import no.nav.tms.varsel.action.*
import no.nav.tms.varsel.builder.InaktiverVarselBuilder
import no.nav.tms.varsel.builder.OpprettVarselBuilder
import no.nav.tms.varsel.builder.OpprettVarselBuilder.EksternVarslingBuilder
import no.nav.tms.varsel.builder.VarselActionBuilder
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class VarselProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {

    private val log = KotlinLogging.logger {}

    fun produceOpprettVarselForUser(innloggetBruker: IdportenUser, dto: ProduceVarselDto) {
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
        return if (dto.javaBuilder) {
            withJavaBuilder(innloggetBruker, eventId, dto)
        } else {
            withKotlinBuilder(innloggetBruker, eventId, dto)
        }
    }

    private fun withKotlinBuilder(innloggetBruker: IdportenUser, eventId: String, dto: ProduceVarselDto): String {
        return VarselActionBuilder.opprett {
            type = parseEnum<Varseltype>(dto.type)
            varselId = eventId
            sensitivitet = mapSensitivitet(innloggetBruker.levelOfAssurance)
            ident = innloggetBruker.ident
            tekst = Tekst(
                spraakkode = dto.spraak,
                tekst = dto.tekst,
                default = true
            )
            link = dto.link
            aktivFremTil = dto.aktivFremTil
             if (dto.eksternVarsling) {
                 eksternVarsling {
                     preferertKanal = dto.prefererteKanaler.map { parseEnum<EksternKanal>(it) }.firstOrNull()
                     smsVarslingstekst = dto.smsVarslingstekst
                     epostVarslingstittel = dto.epostVarslingstittel
                     epostVarslingstekst = dto.epostVarslingstekst
                     kanBatches = dto.kanBatches
                     utsettSendingTil = dto.utsettSendingTil
                 }
            }
        }
    }

    private fun withJavaBuilder(innloggetBruker: IdportenUser, eventId: String, dto: ProduceVarselDto): String {
        return OpprettVarselBuilder.newInstance()
            .withType(parseEnum<Varseltype>(dto.type))
            .withVarselId(eventId)
            .withSensitivitet(mapSensitivitet(innloggetBruker.levelOfAssurance))
            .withIdent(innloggetBruker.ident)
            .withTekst(dto.spraak, dto.tekst, true)
            .withLink(dto.link)
            .withAktivFremTil(dto.aktivFremTil)
            .setEksternVarsling(dto)
            .build()
    }

    private fun OpprettVarselBuilder.setEksternVarsling(dto: ProduceVarselDto): OpprettVarselBuilder {
        return if (dto.eksternVarsling) {
            withEksternVarsling(
                OpprettVarselBuilder.eksternVarsling()
                    .withPreferertKanal(dto.prefererteKanaler.map { parseEnum<EksternKanal>(it) }.firstOrNull())
                    .withSmsVarslingstekst(dto.smsVarslingstekst)
                    .withEpostVarslingstittel(dto.epostVarslingstekst)
                    .withEpostVarslingstekst(dto.epostVarslingstittel)
                    .withKanBatches(dto.kanBatches)
                    .withUtsettSendingTil(dto.utsettSendingTil)
            )
        } else {
            this
        }
    }
    private inline fun <reified T: Enum<T>> parseEnum(string: String): T {
        return enumValues<T>()
            .firstOrNull { it.name.lowercase() == string.lowercase() }
            ?: throw IllegalArgumentException("$string er ikke en gyldig ${T::class.simpleName}")
    }

    private fun mapSensitivitet(loa: LevelOfAssurance): Sensitivitet {
       return when(loa) {
           LevelOfAssurance.SUBSTANTIAL -> Sensitivitet.Substantial
           LevelOfAssurance.HIGH -> Sensitivitet.High
       }
    }
}
