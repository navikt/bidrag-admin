@file:Suppress("ktlint:standard:filename")

package no.nav.bidrag.admin.dto

import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import java.time.LocalDate

data class OppdaterEndringsloggRequest(
    val tittel: String? = null,
    val tilhørerSkjermbilde: EndringsloggTilhørerSkjermbilde? = null,
    val sammendrag: String? = null,
    val erPåkrevd: Boolean? = null,
    val aktivFraTidspunkt: LocalDate? = null,
    val aktivTilTidspunkt: LocalDate? = null,
    val endringer: List<OppdaterEndringsloggEndring>? = null,
)

data class OppdaterEndringsloggEndring(
    val id: Long,
    val tittel: String? = null,
    val innhold: String? = null,
)

data class OpprettEndringsloggRequest(
    val tittel: String,
    val tilhørerSkjermbilde: EndringsloggTilhørerSkjermbilde,
    val sammendrag: String,
    val erPåkrevd: Boolean = false,
    val aktivFraTidspunkt: LocalDate? = null,
    val aktivTilTidspunkt: LocalDate? = null,
)

data class LeggTilEndringsloggEndring(
    val tittel: String,
    val innhold: String,
)
