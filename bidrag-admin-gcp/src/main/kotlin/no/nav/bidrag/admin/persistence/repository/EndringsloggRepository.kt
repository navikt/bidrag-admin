package no.nav.bidrag.admin.persistence.repository

import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface EndringsloggRepository : CrudRepository<Endringslogg, Long> {
    @Query(
        "select e from endringslogg e where " +
            "(:#{#type.isEmpty()} = true or e.tilhørerSkjermbilde in :type) and " +
            "e.aktivFraTidspunkt is not null and e.aktivFraTidspunkt <= current_date and " +
            "(e.aktivTilTidspunkt is null or e.aktivTilTidspunkt > current_date)",
    )
    fun findAllAktiveByTilhørerSkjermbilde(type: List<EndringsloggTilhørerSkjermbilde>): List<Endringslogg>

    @Query(
        "select e from endringslogg e where (:#{#type.isEmpty()} = true or e.tilhørerSkjermbilde in :type)",
    )
    fun findAllByTilhørerSkjermbilde(type: List<EndringsloggTilhørerSkjermbilde>): List<Endringslogg>
}
