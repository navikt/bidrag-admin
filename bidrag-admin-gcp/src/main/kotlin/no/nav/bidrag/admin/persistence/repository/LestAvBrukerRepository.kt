package no.nav.bidrag.admin.persistence.repository

import no.nav.bidrag.admin.persistence.entity.Driftsmelding
import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.admin.persistence.entity.LestAvBruker
import no.nav.bidrag.admin.persistence.entity.Person
import org.springframework.data.repository.CrudRepository

interface LestAvBrukerRepository : CrudRepository<LestAvBruker, Long> {
    fun findByPersonAndEndringslogg(
        person: Person,
        endringslogg: Endringslogg,
    ): LestAvBruker?

    fun findByPersonAndDriftsmelding(
        person: Person,
        driftsmelding: Driftsmelding,
    ): LestAvBruker?
}
