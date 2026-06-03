package no.nav.bidrag.produksjonsoppfølging.repository

import no.nav.bidrag.produksjonsoppfølging.domene.Søknadslinje
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SøknadslinjeRepository : CrudRepository<Søknadslinje, Long> {
    fun findBySaksnr(saksnr: String?): MutableList<Søknadslinje>
}
