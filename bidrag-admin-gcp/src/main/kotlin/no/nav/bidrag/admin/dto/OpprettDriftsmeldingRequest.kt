package no.nav.bidrag.admin.dto

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.bidrag.admin.persistence.entity.DriftsmeldingStatus
import java.time.LocalDate

data class OpprettDriftsmeldingRequest(
    val tittel: String,
    val aktivFraTidspunkt: LocalDate? = null,
    val aktivTilTidspunkt: LocalDate? = null,
    val innhold: String,
    val status: DriftsmeldingStatus,
)

data class OppdaterDriftsmeldingRequest(
    val tittel: String? = null,
    val aktivFraTidspunkt: LocalDate? = null,
    val aktivTilTidspunkt: LocalDate? = null,
    val historikk: List<OppdaterDriftsmeldingHistorikkRequest>? = null,
)

data class OppdaterDriftsmeldingHistorikkRequest(
    @Schema(description = "Er bare påkrevd hvis du oppdaterer hele driftsmeldingen")
    val id: Long? = null,
    val innhold: String? = null,
    val aktivFraTidspunkt: LocalDate? = null,
    val aktivTilTidspunkt: LocalDate? = null,
    val status: DriftsmeldingStatus? = null,
)

data class LeggTilDriftsmeldingHistorikkRequest(
    val innhold: String,
    val aktivFraTidspunkt: LocalDate? = null,
    val aktivTilTidspunkt: LocalDate? = null,
    val status: DriftsmeldingStatus,
)
