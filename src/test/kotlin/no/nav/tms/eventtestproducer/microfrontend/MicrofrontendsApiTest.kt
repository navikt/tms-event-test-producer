package no.nav.tms.eventtestproducer.microfrontend

import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.plugin
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.mockk
import io.mockk.verify
import no.nav.tms.token.support.idporten.sidecar.mock.SecurityLevel
import no.nav.tms.token.support.idporten.sidecar.mock.installIdPortenAuthMock
import org.amshove.kluent.shouldBe
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory

class MicrofrontendsApiTest {

    private val testLogger = LoggerFactory.getLogger(MicrofrontendProducer::class.java)
    private val producer = mockk<MicrofrontendProducer>(relaxed = true)

    @Test
    fun testPostMicrofrontendDisable() = testApplication {
        setupMicrofrontendApi()

        client.post("/microfrontend/mk1/disable").apply {
            status shouldBe HttpStatusCode.OK
        }

        verify(exactly = 1) { producer.produceDisable("12345678910","mk1") }
    }

    @Test
    fun testPostMicrofrontendEnable() = testApplication {
        setupMicrofrontendApi()
        client.post("/microfrontend/mk4/enable").apply {
            status shouldBe HttpStatusCode.OK

        }
        verify(exactly = 1) { producer.produceEnable("12345678910","mk4") }
    }

    fun ApplicationTestBuilder.setupMicrofrontendApi() = application {
        installIdPortenAuthMock {
            setAsDefault = true
            staticSecurityLevel = SecurityLevel.LEVEL_4
            alwaysAuthenticated = true
            staticUserPid = "12345678910"
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


private fun allRoutes(root: Route): List<Route> = listOf(root) + root.children.flatMap { allRoutes(it) }