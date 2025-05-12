package no.nav.bidrag.admin.persistence.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDate

@Entity(name = "driftsmelding")
class Driftsmelding(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    open var opprettetTidspunkt: LocalDate = LocalDate.now(),
    open var aktivFraTidspunkt: LocalDate? = null,
    open var aktivTilTidspunkt: LocalDate? = null,
    var tittel: String,
    var innhold: String,
    val opprettetAv: String,
    val opprettetAvNavn: String,
    @OneToMany(
        mappedBy = "driftsmelding",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    )
    val brukerLesinger: MutableSet<LestAvBruker> = mutableSetOf(),
)
