package no.nav.tms.eventtestproducer.common

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.tms.eventtestproducer.tokenx.AccessToken
import java.net.URL

suspend inline fun <reified T> HttpClient.get(url: URL, accessToken: AccessToken): T = withContext(
    Dispatchers.IO) {
    request<T> {
        url(url)
        method = HttpMethod.Get
        header(HttpHeaders.Authorization, "Bearer ${accessToken.value}")
        timeout {
            socketTimeoutMillis = 30000
            connectTimeoutMillis = 10000
            requestTimeoutMillis = 40000
        }
    }
}
