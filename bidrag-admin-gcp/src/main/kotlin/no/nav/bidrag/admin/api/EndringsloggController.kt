package no.nav.bidrag.admin.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.bidrag.admin.dto.OppdaterEndringsloggRequest
import no.nav.bidrag.admin.dto.OpprettEndringsloggRequest
import no.nav.bidrag.admin.dto.toDto
import no.nav.bidrag.admin.persistence.entity.AktivForMiljø
import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import no.nav.bidrag.admin.service.EndringsloggService
import no.nav.security.token.support.core.api.Protected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Protected
@RequestMapping("/endringslogg")
class EndringsloggController(
    private val endringsloggService: EndringsloggService,
) {
    @GetMapping
    @Operation(
        summary = "Hent liste over endringslogg for en skjermbilde eller alle",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun hentAlleEndringslogg(
        @RequestParam(required = false) skjermbilde: EndringsloggTilhørerSkjermbilde?,
        @RequestParam(required = false) bareAktive: Boolean = true,
    ) = endringsloggService
        .hentAlleForType(skjermbilde, bareAktive)
        .sortedByDescending { it.aktivFraTidspunkt }
        .map { it.toDto() }

    @GetMapping("/{endringsloggId}")
    @Operation(
        summary = "Hent en enkel endringslogg med id",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun hentEndringslogg(
        @PathVariable endringsloggId: Long,
    ) = endringsloggService.hentEndringslogg(endringsloggId).toDto()

    @PostMapping("/{endringsloggId}/lest")
    @Operation(
        summary = "Oppdater endringslogg at den er lest av bruker. Brukerdetaljer hentes fra token",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun oppdaterLestAvBrukerEndringslogg(
        @PathVariable endringsloggId: Long,
    ) = endringsloggService.oppdaterLestAvBruker(endringsloggId).toDto()

    @PostMapping
    @Operation(
        summary = "Opprett endringslogg",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun opprettEndringslogg(
        @RequestBody request: OpprettEndringsloggRequest,
    ) = endringsloggService.opprettEndringslogg(request).toDto()

    @PutMapping("/{endringsloggId}")
    @Operation(
        summary = "Oppdater endringslogg. Kan også bruke til å endre på rekkefølgene til endringene",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun oppdaterEndringslogg(
        @PathVariable endringsloggId: Long,
        @RequestBody request: OppdaterEndringsloggRequest,
    ) = endringsloggService.oppdaterEndringslogg(endringsloggId, request).toDto()

    @PatchMapping("/{endringsloggId}/deaktiver")
    @Operation(
        summary = "Deaktiver endringslogg",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun deaktiverEndringslogg(
        @PathVariable endringsloggId: Long,
        @RequestParam(required = false) environment: AktivForMiljø = AktivForMiljø.DEV,
    ) = endringsloggService.deaktiverEndringslogg(endringsloggId, environment).toDto()

    @PatchMapping("/{endringsloggId}/aktiver")
    @Operation(
        summary = "Aktiver endringslogg",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    fun aktiverEndringslogg(
        @PathVariable endringsloggId: Long,
        @RequestParam(required = false) environment: AktivForMiljø = AktivForMiljø.DEV,
    ) = endringsloggService.aktiverEndringslogg(endringsloggId, environment).toDto()
}
