package no.nav.tms.eventtestproducer.statuskort

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tms.statuskort.builder.StatuskortBuilder
import no.nav.tms.statuskort.event.Sensitivitet
import no.nav.tms.token.support.user.token.verification.LevelOfAssurance
import no.nav.tms.token.support.user.token.verification.UserPrincipal
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class StatuskortProducer(
    private val kafkaProducer: Producer<String, String>,
    private val topicName: String
) {

    private val log = KotlinLogging.logger {}

    fun opprettStatuskort(user: UserPrincipal, innhold: Innhold, tjenesteLabel: String): String {

        val generatedId = UUID.randomUUID().toString()

        StatuskortBuilder.opprett {
            ident = user.ident
            statuskortId = generatedId
            sensitivitet = mapSensitivitet(user.levelOfAssurance)
            tjeneste = tjenesteLabel
            innhold {
                bokmaal {
                    tittel = innhold.nb.tittel
                    beskrivelse = innhold.nb.beskrivelse
                    link = innhold.nb.link
                }
                nynorsk {
                    tittel = innhold.nn.tittel
                    beskrivelse = innhold.nn.beskrivelse
                    link = innhold.nn.link
                }
                engelsk {
                    tittel = innhold.en.tittel
                    beskrivelse = innhold.en.beskrivelse
                    link = innhold.en.link
                }
            }
        }.let {
            sendEvent(generatedId, it)
        }

        log.info { "Statuskort med id [$generatedId] er opprettet" }

        return generatedId
    }

    fun oppdaterStatuskort(requestStatuskortId: String, innhold: Innhold): String {

        StatuskortBuilder.oppdater {
            statuskortId = requestStatuskortId
            innhold {
                bokmaal {
                    tittel = innhold.nb.tittel
                    beskrivelse = innhold.nb.beskrivelse
                    link = innhold.nb.link
                }
                nynorsk {
                    tittel = innhold.nn.tittel
                    beskrivelse = innhold.nn.beskrivelse
                    link = innhold.nn.link
                }
                engelsk {
                    tittel = innhold.en.tittel
                    beskrivelse = innhold.en.beskrivelse
                    link = innhold.en.link
                }
            }
        }.let {
            sendEvent(requestStatuskortId, it)
        }

        log.info { "Statuskort med id [$requestStatuskortId] er oppdatert" }

        return requestStatuskortId
    }

    fun inaktiverStatuskort(requestStatuskortId: String): String {

        sendEvent(
            key = requestStatuskortId,
            event = StatuskortBuilder.inaktiver {
                statuskortId = requestStatuskortId
            }
        )

        log.info { "Statuskort med id [$requestStatuskortId] er inaktivert" }

        return requestStatuskortId
    }

    private fun sendEvent(key: String, event: String) {
        kafkaProducer.send(ProducerRecord(topicName, key, event))
    }

    private fun mapSensitivitet(loa: LevelOfAssurance): Sensitivitet {
       return when(loa) {
           LevelOfAssurance.Substantial -> Sensitivitet.Substantial
           LevelOfAssurance.High -> Sensitivitet.High
       }
    }
}
