package no.nav.bidrag.admin.service

import no.nav.bidrag.admin.consumer.BisysConsumer
import org.springframework.stereotype.Service

@Service
class BatchService(
    private val bisysConsumer: BisysConsumer,
) {
    fun getJobNames(): List<String> = bisysConsumer.getJobNames()
}
