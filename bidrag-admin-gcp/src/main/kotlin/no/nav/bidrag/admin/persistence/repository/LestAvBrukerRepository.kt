package no.nav.bidrag.admin.persistence.repository

import no.nav.bidrag.admin.persistence.entity.DriftsmeldingHistorikk
import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.admin.persistence.entity.EndringsloggEndring
import no.nav.bidrag.admin.persistence.entity.LestAvBruker
import no.nav.bidrag.admin.persistence.entity.Person
import org.springframework.data.repository.CrudRepository

interface LestAvBrukerRepository : CrudRepository<LestAvBruker, Long> {
    fun findByPersonAndEndringsloggEndring(
        person: Person,
        endring: EndringsloggEndring,
    ): LestAvBruker?

    fun findByPersonAndEndringslogg(
        person: Person,
        endring: Endringslogg,
    ): LestAvBruker?

    fun findByPersonAndDriftsmeldingHistorikk(
        person: Person,
        driftsmelding: DriftsmeldingHistorikk,
    ): LestAvBruker?
}
