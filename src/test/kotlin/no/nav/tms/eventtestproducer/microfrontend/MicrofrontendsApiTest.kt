package no.nav.tms.eventtestproducer.microfrontend

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.plugin
import io.ktor.server.auth.*
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.mockk
import io.mockk.verify
import no.nav.tms.token.support.idporten.sidecar.mock.LevelOfAssurance
import no.nav.tms.token.support.idporten.sidecar.mock.idPortenMock
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory

class MicrofrontendsApiTest {

    private val testLogger = LoggerFactory.getLogger(MicrofrontendProducer::class.java)
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
                this shouldContainAll listOf("microfrontend_id", "@initiated_by", "ident", "@action")
                size shouldBeEqualTo 4
            }
            this["microfrontend_id"].asText() shouldBeEqualTo "mk4"
            this["ident"].asText() shouldBeEqualTo "123"
            this["@action"].asText() shouldBeEqualTo "disable"
            this["@initiated_by"].asText() shouldBeEqualTo "minside-testproducer"
        }

        objectMapper.readTree(v2Disable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll listOf("microfrontend_id", "initiated_by", "ident", "@action")
                size shouldBeEqualTo 4
            }
            this["microfrontend_id"].asText() shouldBeEqualTo "mk4"
            this["ident"].asText() shouldBeEqualTo "123"
            this["@action"].asText() shouldBeEqualTo "disable"
            this["initiated_by"].asText() shouldBeEqualTo "minside-testproducer"
        }

        objectMapper.readTree(v1Disable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll listOf("microfrontend_id", "ident", "@action")
                size shouldBeEqualTo 3
            }
            this["microfrontend_id"].asText() shouldBeEqualTo "mk4"
            this["ident"].asText() shouldBeEqualTo "123"
            this["@action"].asText() shouldBeEqualTo "disable"
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
                size shouldBeEqualTo 5
            }
            this["microfrontend_id"].asText() shouldBeEqualTo "mk4"
            this["ident"].asText() shouldBeEqualTo "123"
            this["@action"].asText() shouldBeEqualTo "enable"
            this["@initiated_by"].asText() shouldBeEqualTo "minside-testproducer"
            this["sensitivitet"].asText() shouldBeEqualTo "high"
        }

        objectMapper.readTree(v2Enable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll listOf("microfrontend_id", "initiated_by", "ident", "@action", "sikkerhetsnivå")
                size shouldBeEqualTo 5
            }
            this["microfrontend_id"].asText() shouldBeEqualTo "mk4"
            this["ident"].asText() shouldBeEqualTo "123"
            this["@action"].asText() shouldBeEqualTo "enable"
            this["initiated_by"].asText() shouldBeEqualTo "minside-testproducer"
            this["sikkerhetsnivå"].asInt() shouldBeEqualTo 4
        }

        objectMapper.readTree(v1Enable("123", "mk4")).apply {
            this.keys().apply {
                this shouldContainAll listOf("microfrontend_id", "ident", "@action")
                size shouldBeEqualTo 3
            }
            this["microfrontend_id"].asText() shouldBeEqualTo "mk4"
            this["ident"].asText() shouldBeEqualTo "123"
            this["@action"].asText() shouldBeEqualTo "enable"
        }

    }

    fun ApplicationTestBuilder.setupMicrofrontendApi() = application {
        authentication {
            idPortenMock {
                setAsDefault = true
                staticLevelOfAssurance = LevelOfAssurance.LEVEL_4
                alwaysAuthenticated = true
                staticUserPid = "12345678910"
            }
        }

        routing {
            authenticate {
                microfrontedApi(producer)
            }
        }

        logRoutes()
    }

    private fun Application.logRoutes() {
        val allRoutes = allRoutes(plugin(Routing))
        val allRoutesWithMethod = allRoutes.filter { it.selector is HttpMethodRouteSelector }
        testLogger.debug { "Application has ${allRoutesWithMethod.size} routes" }

        allRoutesWithMethod.forEach {
            testLogger.debug { "route: $it" }
        }
    }

}

private fun JsonNode.keys(): MutableList<String> {
    val keys = mutableListOf<String>()
    fieldNames().forEachRemaining { key -> keys.add(key) }
    return keys
}


private fun allRoutes(root: Route): List<Route> = listOf(root) + root.children.flatMap { allRoutes(it) }
