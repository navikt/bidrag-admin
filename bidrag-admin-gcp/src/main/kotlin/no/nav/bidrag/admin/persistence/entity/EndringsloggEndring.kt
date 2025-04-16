package no.nav.bidrag.admin.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity(name = "endringslogg_endring")
data class EndringsloggEndrng(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endringslogg_id", nullable = false)
    open val endringslogg: Endringslogg,
    val rekkefølgeIndeks: Int,
    val innhold: String,
    val tittel: String,
    open var opprettetTidspunkt: LocalDate,
)
