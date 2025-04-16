package no.nav.bidrag.admin.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.bidrag.admin.service.BatchService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/batch")
@Unprotected
class BisysBatchController(
    val service: BatchService,
) {
    @GetMapping
    @Operation(
        description = "Henter alle batchnavn fra Bisys",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun getBatchNames() = service.getJobNames()
}
