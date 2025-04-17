package no.nav.bidrag.admin.consumer

import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.io.HttpClientConnectionManager
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier
import org.apache.hc.core5.ssl.SSLContexts
import org.apache.hc.core5.ssl.TrustStrategy
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import javax.net.ssl.SSLContext

@Service
class BisysConsumer(
    @Value("\${BISYS_URL}") private val bisysBaseUrl: String,
) {
    val urlBuilder get() =
        UriComponentsBuilder
            .fromPath("https://$bisysBaseUrl")
            .port("9445")
            .pathSegment("rtv-bidrag-batch", "rest", "batch")

    fun getJobNames(): List<String> =
        getRestTemplate().getForEntity(urlBuilder.pathSegment("jobs").build().toUri(), List::class.java).body as List<String>

    fun getRunningExecutions(jobName: String): List<Long> =
        getRestTemplate()
            .getForEntity(
                urlBuilder.pathSegment("running", "executions", jobName).build().toUri(),
                List::class.java,
            ).body as List<Long>

    fun launchJob(jobName: String): Long? =
        getRestTemplate()
            .getForEntity(
                urlBuilder.pathSegment("launch", jobName).build().toUri(),
                Long::class.java,
            ).body

    fun executionStatus(executionId: String): Int? =
        getRestTemplate()
            .getForEntity(
                urlBuilder.pathSegment("execution", executionId, "status").build().toUri(),
                Int::class.java,
            ).body

    fun stopExecution(executionId: String): Boolean? =
        getRestTemplate()
            .getForEntity(
                urlBuilder.pathSegment("execution", executionId, "stop").build().toUri(),
                Boolean::class.java,
            ).body

    fun exectionParameters(executionId: String): String? =
        getRestTemplate()
            .getForEntity(
                urlBuilder.pathSegment("execution", executionId, "parameters").build().toUri(),
                String::class.java,
            ).body

    fun getRestTemplate(): RestTemplate {
        val acceptingTrustStrategy = TrustStrategy { x509Certificates, s -> true }
        val sslContext: SSLContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build()
        val csf = DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier())
        val requestFactory = HttpComponentsClientHttpRequestFactory()
        val connectionManager: HttpClientConnectionManager =
            PoolingHttpClientConnectionManagerBuilder
                .create()
                .setTlsSocketStrategy(
                    csf,
                ).build()
        val httpClient = HttpClients.custom().setConnectionManager(connectionManager).build()
        requestFactory.httpClient = httpClient
        return RestTemplate(requestFactory)
    }
}
