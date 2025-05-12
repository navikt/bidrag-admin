package no.nav.bidrag.admin.dto

import no.nav.bidrag.admin.persistence.entity.Driftsmelding
import no.nav.bidrag.commons.security.utils.TokenUtils
import java.time.LocalDate

data class DriftsmeldingDto(
    val id: Long,
    val dato: LocalDate,
    val tittel: String,
    val innhold: String,
    val erLestAvBruker: Boolean,
)

fun Driftsmelding.toDto() =
    DriftsmeldingDto(
        id = id ?: -1,
        dato = aktivFraTidspunkt ?: opprettetTidspunkt,
        tittel = tittel,
        innhold = innhold,
        erLestAvBruker =
            brukerLesinger.any {
                it.person.navIdent == TokenUtils.hentSaksbehandlerIdent() || TokenUtils.erApplikasjonsbruker()
            },
    )
