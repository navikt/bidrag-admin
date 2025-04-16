package no.nav.bidrag.admin.persistence.entity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity(name = "endringslogg_bruker_lesing")
data class LestAvBruker(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endringslogg_id", nullable = false)
    val endringslogg: Endringslogg? = null,
    @Column(name = "ident", nullable = false)
    val ident: String,
    @Column(name = "navn", nullable = false)
    val navn: String,
    @Column(name = "lest_tidspunkt")
    val lestTidspunkt: LocalDateTime = LocalDateTime.now(),
)
