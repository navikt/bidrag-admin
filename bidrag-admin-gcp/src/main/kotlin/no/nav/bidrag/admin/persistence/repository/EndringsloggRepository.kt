package no.nav.bidrag.admin.persistence.repository

import no.nav.bidrag.admin.persistence.entity.AktivForMiljø
import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface EndringsloggRepository : CrudRepository<Endringslogg, Long> {
    @Query(
        nativeQuery = true,
        value = """
        select * from endringslogg e
        where 
            (:typeIsEmpty = true or e.tilhører_skjermbilde = any(:type)) and
            e.aktiv_fra_tidspunkt is not null and
            e.aktiv_fra_tidspunkt <= current_date and
            (e.aktiv_til_tidspunkt is null or e.aktiv_til_tidspunkt > current_date) and
            :env = any(e.aktiv_for_miljø)
    """,
    )
    fun findAllAktiveByTilhørerSkjermbilde(
        @Param("type") type: List<String>,
        @Param("env") env: String,
        @Param("typeIsEmpty") typeIsEmpty: Boolean = type.isEmpty(),
    ): List<Endringslogg>

    @Query(
        "select e from endringslogg e where (:#{#type.isEmpty()} = true or e.tilhørerSkjermbilde in :type)",
    )
    fun findAllByTilhørerSkjermbilde(type: List<EndringsloggTilhørerSkjermbilde>): List<Endringslogg>
}
