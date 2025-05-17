package no.nav.bidrag.admin.persistence.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import no.nav.bidrag.commons.security.utils.TokenUtils
import java.time.LocalDate

@Entity(name = "endringslogg")
class Endringslogg(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @OneToMany(
        fetch = FetchType.EAGER,
        mappedBy = "endringslogg",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    open var endringer: MutableSet<EndringsloggEndring> = mutableSetOf(),
    open var opprettetTidspunkt: LocalDate = LocalDate.now(),
    open var aktivFraTidspunkt: LocalDate? = null,
    open var aktivTilTidspunkt: LocalDate? = null,
    @Enumerated(EnumType.STRING)
    open var tilhørerSkjermbilde: EndringsloggTilhørerSkjermbilde,
    var tittel: String,
    var sammendrag: String,
    var innhold: String? = null,
    var erPåkrevd: Boolean = false,
    @Enumerated(EnumType.STRING)
    val endringstype: List<Endringstype> = listOf(Endringstype.ENDRING),
    val opprettetAv: String,
    val opprettetAvNavn: String,
    @OneToMany(
        mappedBy = "endringslogg",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    )
    val brukerLesinger: MutableSet<LestAvBruker> = mutableSetOf(),
) {
    val erAlleLestAvBruker get() =
        endringer.all { e ->
            e.brukerLesinger.any {
                it.person.navIdent ==
                    TokenUtils.hentSaksbehandlerIdent()
            }
        }

    override fun toString(): String =
        "Endringslogg(id=$id, tittel='$tittel', " +
            "sammendrag='$sammendrag', opprettetTidspunkt=$opprettetTidspunkt, " +
            "aktivFraTidspunkt=$aktivFraTidspunkt, aktivTilTidspunkt=$aktivTilTidspunkt, " +
            "tilhørerSkjermbilde=$tilhørerSkjermbilde, erPåkrevd=$erPåkrevd, erAlleLestAvBruker: $erAlleLestAvBruker)"
}

@Schema(enumAsRef = true)
enum class Endringstype {
    NYHET,
    ENDRING,
    FEILFIKS,
}

@Schema(enumAsRef = true)
enum class EndringsloggTilhørerSkjermbilde {
    BEHANDLING_BIDRAG,
    BEHANDLING_FORSKUDD,
    BEHANDLING_SÆRBIDRAG,
    BEHANDLING_ALLE,
    FORSENDELSE,
    INNSYN_DOKUMENT,
    SAMHANDLER,
}
