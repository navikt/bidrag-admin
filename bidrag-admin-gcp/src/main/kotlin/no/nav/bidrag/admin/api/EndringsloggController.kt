package no.nav.bidrag.admin.api

import no.nav.bidrag.admin.dto.HentEndringsloggRequest
import no.nav.bidrag.admin.dto.LeggTilEndringsloggEndring
import no.nav.bidrag.admin.dto.OppdaterEndringsloggRequest
import no.nav.bidrag.admin.dto.OpprettEndringsloggRequest
import no.nav.bidrag.admin.dto.toDto
import no.nav.bidrag.admin.service.EndringsloggService
import no.nav.security.token.support.core.api.Protected
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Protected
class EndringsloggController(
    private val endringsloggService: EndringsloggService,
) {
    @PostMapping("/endringslogg/liste")
    fun hentAlleEndringslogg(
        @RequestBody request: HentEndringsloggRequest,
    ) = endringsloggService.hentAlleForType(request.type)

    @GetMapping("/endringslogg/{endringsloggId}")
    fun hentEndringslogg(
        @PathVariable endringsloggId: Long,
    ) = endringsloggService.hentEndringslogg(endringsloggId).toDto()

    @PostMapping("/endringslogg/{endringsloggId}/lest")
    fun oppdaterLestAvBruker(
        @PathVariable endringsloggId: Long,
    ) = endringsloggService.oppdaterLestAvBruker(endringsloggId)

    @PostMapping("/endringslogg")
    fun opprettEndringslogg(
        @RequestBody request: OpprettEndringsloggRequest,
    ) = endringsloggService.opprettEndringslogg(request).toDto()

    @PutMapping("/endringslogg/{endringsloggId}")
    fun oppdaterEndringslogg(
        @PathVariable endringsloggId: Long,
        @RequestBody request: OppdaterEndringsloggRequest,
    ) = endringsloggService.oppdaterEndringslogg(endringsloggId, request).toDto()

    @DeleteMapping("/endringslogg/{endringsloggId}/endring/{endringId}")
    fun slettEndring(
        @PathVariable endringsloggId: Long,
        @PathVariable endringId: Long,
    ) = endringsloggService.slettEndring(endringsloggId, endringId).toDto()

    @PutMapping("/endringslogg/{endringsloggId}/endring")
    fun leggTilEndring(
        @PathVariable endringsloggId: Long,
        @RequestBody request: LeggTilEndringsloggEndring,
    ) = endringsloggService.leggTilEndringsloggEndring(endringsloggId, request).toDto()

    @PatchMapping("/endringslogg/{endringsloggId}/deaktiver")
    fun deaktiverEndringslogg(
        @PathVariable endringsloggId: Long,
    ) = endringsloggService.deaktiverEndringslogg(endringsloggId).toDto()

    @PatchMapping("/endringslogg/{endringsloggId}/aktiver")
    fun aktiverEndringslogg(
        @PathVariable endringsloggId: Long,
    ) = endringsloggService.aktiverEndringslogg(endringsloggId).toDto()
}
