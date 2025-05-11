package no.nav.bidrag.admin.persistence.repository

import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface EndringsloggRepository : CrudRepository<Endringslogg, Long> {
    @Query(
        "select e from endringslogg e where e.tilhørerSkjermbilde = :type and " +
            "e.aktivFraTidspunkt is not null and e.aktivFraTidspunkt <= current_date " +
            "and e.aktivTilTidspunkt is null or e.aktivTilTidspunkt > current_date",
    )
    fun findAllByTilhørerSkjermbilde(type: EndringsloggTilhørerSkjermbilde): List<Endringslogg>
}
