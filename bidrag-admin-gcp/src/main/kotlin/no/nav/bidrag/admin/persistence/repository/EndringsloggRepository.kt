package no.nav.bidrag.admin.persistence.repository

import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import org.springframework.data.repository.CrudRepository

interface EndringsloggRepository : CrudRepository<Endringslogg, Long> {
    fun findALlByTilhørerSkjermbilde(type: EndringsloggTilhørerSkjermbilde): List<Endringslogg>
}
