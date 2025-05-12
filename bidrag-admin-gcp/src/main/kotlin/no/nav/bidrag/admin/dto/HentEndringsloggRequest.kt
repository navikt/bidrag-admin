package no.nav.bidrag.admin.dto

import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde

data class HentEndringsloggRequest(
    val skjermbilde: EndringsloggTilhørerSkjermbilde,
)
