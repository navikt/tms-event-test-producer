package no.nav.tms.eventtestproducer.microfrontend

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.*
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.mockk
import io.mockk.verify
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.idPortenMock
import org.junit.jupiter.api.Test

class MicrofrontendsApiTest {

    private val producer = mockk<MicrofrontendProducer>(relaxed = true)
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `disable meldinger`() = testApplication {
        setupMicrofrontendApi()

        client.post("/microfrontend/mk1/disable").status shouldBe HttpStatusCode.OK
        verify(exactly = 1) { producer.produceDisable("12345678910", "mk1",::v3Disable) }
        client.post("/microfrontend/mk1/disable?version=2").status shouldBe HttpStatusCode.OK
        verify(exactly = 1) { producer.produceDisable("12345678910", "mk1",::v2Disable) }
        client.post("/microfrontend/mk1/disable?version=1").status shouldBe HttpStatusCode.OK
        verify(exactly = 1) { producer.produceDisable("12345678910", "mk1",::v1Disable) }

        objectMapper.readTree(v3Disable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll  listOf("microfrontend_id", "@initiated_by", "ident", "@action")
                size shouldBe 4
            }
            this["microfrontend_id"].asText() shouldBe "mk4"
            this["ident"].asText() shouldBe "123"
            this["@action"].asText() shouldBe "disable"
            this["@initiated_by"].asText() shouldBe "minside-testproducer"
        }

        objectMapper.readTree(v2Disable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll listOf("microfrontend_id", "initiated_by", "ident", "@action")
                size shouldBe 4
            }
            this["microfrontend_id"].asText() shouldBe "mk4"
            this["ident"].asText() shouldBe "123"
            this["@action"].asText() shouldBe "disable"
            this["initiated_by"].asText() shouldBe "minside-testproducer"
        }

        objectMapper.readTree(v1Disable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll listOf("microfrontend_id", "ident", "@action")
                size shouldBe 3
            }
            this["microfrontend_id"].asText() shouldBe "mk4"
            this["ident"].asText() shouldBe "123"
            this["@action"].asText() shouldBe "disable"
        }


    }

    @Test
    fun `enable meldinger`() = testApplication {
        setupMicrofrontendApi()
        client.post("/microfrontend/mk4/enable").status shouldBe HttpStatusCode.OK
        verify(exactly = 1) { producer.produceEnable("12345678910", "mk4", ::v3Enable) }

        client.post("/microfrontend/mk4/enable?version=2").status shouldBe HttpStatusCode.OK
        verify(exactly = 1) { producer.produceEnable("12345678910", "mk4", ::v2Enable) }

        client.post("/microfrontend/mk4/enable?version=1").status shouldBe HttpStatusCode.OK
        verify(exactly = 1) { producer.produceEnable("12345678910", "mk4", ::v1Enable) }



        objectMapper.readTree(v3Enable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll listOf("microfrontend_id", "@initiated_by", "ident", "@action", "sensitivitet")
                size shouldBe 5
            }
            this["microfrontend_id"].asText() shouldBe "mk4"
            this["ident"].asText() shouldBe "123"
            this["@action"].asText() shouldBe "enable"
            this["@initiated_by"].asText() shouldBe "minside-testproducer"
            this["sensitivitet"].asText() shouldBe "high"
        }

        objectMapper.readTree(v2Enable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll listOf("microfrontend_id", "initiated_by", "ident", "@action", "sikkerhetsnivå")
                size shouldBe 5
            }
            this["microfrontend_id"].asText() shouldBe "mk4"
            this["ident"].asText() shouldBe "123"
            this["@action"].asText() shouldBe "enable"
            this["initiated_by"].asText() shouldBe "minside-testproducer"
            this["sikkerhetsnivå"].asInt() shouldBe 4
        }

        objectMapper.readTree(v1Enable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll listOf("microfrontend_id", "ident", "@action")
                size shouldBe 3
            }
            this["microfrontend_id"].asText() shouldBe "mk4"
            this["ident"].asText() shouldBe "123"
            this["@action"].asText() shouldBe "enable"
        }

    }

    fun ApplicationTestBuilder.setupMicrofrontendApi() = application {
        authentication {
            idPortenMock {
                setAsDefault = true
                staticLevelOfAssurance = LevelOfAssurance.HIGH
                alwaysAuthenticated = true
                staticUserPid = "12345678910"
            }
        }

        routing {
            authenticate {
                microfrontedApi(producer)
            }
        }
    }
}

private fun JsonNode.keys(): MutableList<String> {
    val keys = mutableListOf<String>()
    fieldNames().forEachRemaining { key -> keys.add(key) }
    return keys
}
