package no.nav.bidrag.admin.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.bidrag.admin.service.BatchService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bisys/batch")
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

    @GetMapping("/launch/{jobName}")
    @Operation(
        description = "Starter en batchjobb i Bisys",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun launchJob(
        @PathVariable jobName: String,
    ) = service.launchJob(jobName)

    @GetMapping("/running/{jobName}")
    @Operation(
        description = "Henter kjørende batchjobber for et gitt jobbnavn i Bisys",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun runningExecutions(
        @PathVariable jobName: String,
    ) = service.runningExecutions(jobName)

    @GetMapping("/stop/{executionId}")
    @Operation(
        description = "Stopper en kjørende batchjobb i Bisys",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun stopExecution(
        @PathVariable executionId: String,
    ) = service.stopExecution(executionId)

    @GetMapping("/parameters/{executionId}")
    @Operation(
        description = "Hent parametere til jobben som kjører",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun executionParameters(
        @PathVariable executionId: String,
    ) = service.stopExecution(executionId)
}
