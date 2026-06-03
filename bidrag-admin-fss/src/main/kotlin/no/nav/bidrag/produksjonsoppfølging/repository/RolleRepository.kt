package no.nav.bidrag.produksjonsoppfølging.repository

import no.nav.bidrag.produksjonsoppfølging.domene.Rolle
import org.springframework.data.repository.CrudRepository

interface RolleRepository : CrudRepository<Rolle, Long> {
    fun findBySaksnr(saksnr: String): List<Rolle>
}
