package no.nav.tms.eventtestproducer.varsel

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.idPortenMock
import no.nav.tms.varsel.builder.BuilderEnvironment
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.text.DateFormat
import java.time.ZonedDateTime

class MicrofrontendsApiTest {

    private val mockProducer = MockProducer(
        false,
        StringSerializer(),
        StringSerializer()
    )

    private val varselProducer = VarselProducer(mockProducer, "test-topic")
    private val objectMapper = jacksonObjectMapper()

    private val ident = "01234567890"

    private val testCluster = "test-cluster"
    private val testNamespace = "test-namespace"
    private val testAppName = "test-appName"

    @AfterEach
    fun cleanup() {
        mockProducer.clear()
    }

    @Test
    fun `oppretter varsel på topic`() = testProducerApi { client ->

        val request = varselRequest(
            type = "beskjed",
            tekst = "Hei, hei!",
            spraak = "nb",
            link = "https://linky",
            eksternVarsling = true,
            preferertKanal = "SMS",
            utsettSendingTil = null,
            kanBatches = true,
            epostVarslingstekst = null,
            epostVarslingstittel = null,
            smsVarslingstekst = "Ess emm ess",
            javaBuilder = false,
        )

        client.post("/produce/varsel") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.let {
            it.status.shouldNotBeNull()
        }

        mockProducer.inspectEvent {
            it["type"].asText() shouldBe "beskjed"
            it["ident"].asText() shouldBe ident
            it["sensitivitet"].asText() shouldBe "high"
            it["tekster"][0]["tekst"].asText() shouldBe "Hei, hei!"
            it["tekster"][0]["spraakkode"].asText() shouldBe "nb"
            it["produsent"]["cluster"].asText() shouldBe testCluster
            it["produsent"]["namespace"].asText() shouldBe testNamespace
            it["produsent"]["appnavn"].asText() shouldBe testAppName
            it["eksternVarsling"]["prefererteKanaler"][0].asText() shouldBe "SMS"
            it["aktivFremTil"] .shouldBeNull()
            it["link"].asText() shouldBe "https://linky"
            it["metadata"]["builder_lang"].asText() shouldBe "kotlin"
        }
    }

    @Test
    fun `oppretter varsel etter kall mot legacy-api for beskjed`() = testProducerApi { client ->

        val beskjed = varselRequest(
            type = "N/A",
            tekst = "Hei, hei!",
        )

        client.post("/produce/beskjed") {
            contentType(ContentType.Application.Json)
            setBody(beskjed)
        }.let {
            it.status.shouldNotBeNull()
        }

        mockProducer.inspectEvent {
            it["type"].asText() shouldBe "beskjed"
            it["tekster"][0]["tekst"].asText() shouldBe "Hei, hei!"
        }
    }

    @Test
    fun `oppretter varsel etter kall mot legacy-api for oppgave`() = testProducerApi { client ->

        val oppgave = varselRequest(
            type = "N/A",
            tekst = "Hei, hei!",
            link = "https://linky"
        )

        client.post("/produce/oppgave") {
            contentType(ContentType.Application.Json)
            setBody(oppgave)
        }.let {
            it.status.shouldNotBeNull()
        }

        mockProducer.inspectEvent {
            it["type"].asText() shouldBe "oppgave"
            it["link"].asText() shouldBe "https://linky"
            it["tekster"][0]["tekst"].asText() shouldBe "Hei, hei!"
        }
    }

    @Test
    fun `oppretter varsel etter kall mot legacy-api for innboks`() = testProducerApi { client ->

        val innboks = varselRequest(
            type = "N/A",
            tekst = "Hei, hei!",
            link = "https://linky"
        )

        client.post("/produce/innboks") {
            contentType(ContentType.Application.Json)
            setBody(innboks)
        }.let {
            it.status.shouldNotBeNull()
        }

        mockProducer.inspectEvent {
            it["type"].asText() shouldBe "innboks"
            it["link"].asText() shouldBe "https://linky"
            it["tekster"][0]["tekst"].asText() shouldBe "Hei, hei!"
        }
    }

    fun testProducerApi(testFunction: suspend (HttpClient) -> Unit) = testApplication {
        BuilderEnvironment.extend(
            mapOf(
                "NAIS_CLUSTER_NAME" to testCluster,
                "NAIS_NAMESPACE" to testNamespace,
                "NAIS_APP_NAME" to testAppName,
            )
        )

        application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                jackson {
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    registerModule(JavaTimeModule())
                    dateFormat = DateFormat.getDateTimeInstance()
                }
            }

            authentication {
                idPortenMock {
                    setAsDefault = true
                    staticLevelOfAssurance = LevelOfAssurance.HIGH
                    alwaysAuthenticated = true
                    staticUserPid = ident
                }
            }

            routing {
                authenticate {
                    varselApi(varselProducer)
                }
            }
        }

        client.config {
            install(ContentNegotiation) {
                jackson {
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    registerModule(JavaTimeModule())
                }
            }
        }.let {
            testFunction.invoke(it)
        }
    }

    private fun varselRequest(
        type: String,
        tekst: String,
        spraak: String = "nb",
        link: String? = null,
        tekster: List<TekstDto> = emptyList(),
        eksternVarsling: Boolean = false,
        preferertKanal: String? = null,
        utsettSendingTil: ZonedDateTime? = null,
        kanBatches: Boolean? = null,
        epostVarslingstekst: String? = null,
        epostVarslingstittel: String? = null,
        smsVarslingstekst: String? = null,
        javaBuilder: Boolean = false,
    ) = ProduceVarselRequest(
        type = type,
        tekst = tekst,
        spraak = spraak,
        _link = link,
        tekster = tekster,
        eksternVarsling = eksternVarsling,
        preferertKanal = preferertKanal,
        utsettSendingTil = utsettSendingTil,
        kanBatches = kanBatches,
        epostVarslingstekst = epostVarslingstekst,
        epostVarslingstittel = epostVarslingstittel,
        smsVarslingstekst = smsVarslingstekst,
        javaBuilder = javaBuilder,
    )

    private fun MockProducer<String, String>.inspectEvent(index: Int? = null, receiver: (JsonNode) -> Unit) {
        val event = if (index != null) {
            history()[index]
        } else {
            history().first()
        }

        event.value()
            .let(objectMapper::readTree)
            .let(receiver)
    }
}

private fun JsonNode.keys(): MutableList<String> {
    val keys = mutableListOf<String>()
    fieldNames().forEachRemaining { key -> keys.add(key) }
    return keys
}
