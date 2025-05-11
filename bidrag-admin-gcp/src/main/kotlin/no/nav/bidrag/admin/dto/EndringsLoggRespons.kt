package no.nav.bidrag.admin.dto

import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.commons.security.utils.TokenUtils

data class EndringsLoggDto(
    val id: Long,
    val tittel: String,
    val sammendrag: String,
    val erPåkrevd: Boolean,
    val endringer: List<EndringsLoggEndringDto>,
    val erLestAvBruker: Boolean,
)

data class EndringsLoggEndringDto(
    val innhold: String,
    val tittel: String,
    val id: Long,
)

fun Endringslogg.toDto() =
    EndringsLoggDto(
        id ?: -1,
        tittel,
        sammendrag,
        erPåkrevd,
        endringer =
            endringer.sortedBy { it.rekkefølgeIndeks }.map {
                EndringsLoggEndringDto(
                    id = it.id!!,
                    innhold = it.innhold,
                    tittel = it.tittel,
                )
            },
        erLestAvBruker =
            brukerLesinger.any {
                it.person.navIdent == TokenUtils.hentSaksbehandlerIdent() || TokenUtils.erApplikasjonsbruker()
            },
    )
