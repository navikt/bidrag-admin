package no.nav.bidrag.admin.api

import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import no.nav.bidrag.admin.service.EndringsloggService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Unprotected
class EndringsloggController(
    private val endringsloggService: EndringsloggService,
) {
    @GetMapping("/endringslogg")
    @Transactional
    fun hentAlleEndringslogg() = endringsloggService.hentAlleForType(EndringsloggTilhørerSkjermbilde.BEHANDLING_ALLE)
}
