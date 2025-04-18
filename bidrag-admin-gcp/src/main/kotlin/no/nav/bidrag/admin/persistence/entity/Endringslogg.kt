package no.nav.bidrag.admin.persistence.entity

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
import java.time.LocalDate

@Entity(name = "endringslogg")
data class Endringslogg(
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
    open var opprettetTidspunkt: LocalDate,
    @Enumerated(EnumType.STRING)
    open var tilhørerSkjermbilde: EndringsloggTilhørerSkjermbilde,
    val tittel: String,
    val sammendrag: String,
    val erPåkrevd: Boolean = false,
    @OneToMany(
        mappedBy = "endringslogg",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    )
    val brukerLesinger: MutableSet<LestAvBruker> = mutableSetOf(),
)

enum class EndringsloggTilhørerSkjermbilde {
    BEHANDLING_BIDRAG,
    BEHANDLING_FORSKUDD,
    BEHANDLING_SÆRBIDRAG,
    BEHANDLING_ALLE,
    FORSENDELSE,
    INNSYN_DOKUMENT,
    SAMHANDLER,
}
