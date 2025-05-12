package no.nav.bidrag.admin.dto

import java.time.LocalDate

data class OpprettDriftsmeldingRequest(
    val tittel: String,
    val innhold: String,
    val aktivFraTidspunkt: LocalDate? = null,
    val aktivTilTidspunkt: LocalDate? = null,
)

data class OppdaterDriftsmeldingRequest(
    val tittel: String? = null,
    val innhold: String? = null,
    val aktivFraTidspunkt: LocalDate? = null,
    val aktivTilTidspunkt: LocalDate? = null,
)
