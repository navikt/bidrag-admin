package no.nav.bidrag.admin.persistence.entity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import java.time.LocalDateTime

@Entity(name = "lest_av_bruker")
data class LestAvBruker(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endringslogg_id", nullable = false)
    val endringslogg: Endringslogg? = null,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    val person: Person,
    @Column(name = "lest_tidspunkt")
    val lestTidspunkt: LocalDateTime = LocalDateTime.now(),
)
