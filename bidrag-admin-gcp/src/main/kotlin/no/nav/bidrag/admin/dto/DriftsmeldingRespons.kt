package no.nav.bidrag.admin.dto

import no.nav.bidrag.admin.persistence.entity.Driftsmelding
import no.nav.bidrag.admin.persistence.entity.DriftsmeldingStatus
import no.nav.bidrag.commons.security.utils.TokenUtils
import java.time.LocalDate

data class DriftsmeldingDto(
    val id: Long,
    val dato: LocalDate,
    val tittel: String,
    val historikk: List<DriftsmeldingHistorikkDto>,
)

data class DriftsmeldingHistorikkDto(
    val id: Long,
    val dato: LocalDate,
    val innhold: String,
    val status: DriftsmeldingStatus,
    val erLestAvBruker: Boolean,
)

fun Driftsmelding.toDto() =
    DriftsmeldingDto(
        id = id ?: -1,
        dato = aktivFraTidspunkt ?: opprettetTidspunkt,
        tittel = tittel,
        historikk =
            historikk
                .sortedBy { it.aktivFraTidspunkt }
                .filter {
                    it.aktivFraTidspunkt != null &&
                        it.aktivFraTidspunkt!! < LocalDate.now() &&
                        (it.aktivTilTidspunkt == null || it.aktivTilTidspunkt!! < LocalDate.now())
                }.map {
                    DriftsmeldingHistorikkDto(
                        id = it.id ?: -1,
                        dato = it.aktivFraTidspunkt ?: it.opprettetTidspunkt,
                        innhold = it.innhold,
                        status = it.status,
                        erLestAvBruker =
                            it.brukerLesinger.any { lesing -> lesing.person.navIdent == TokenUtils.hentSaksbehandlerIdent() },
                    )
                },
    )
