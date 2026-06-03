package no.nav.bidrag.produksjonsoppfølging.oppfølginger.vedtakoverføring

import no.nav.bidrag.produksjonsoppfølging.domene.VedtakOverføring
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VedtakoverføringRepository : JpaRepository<VedtakOverføring, Int> {
    @Query("FROM VedtakOverføring WHERE status = 'FEILET'")
    fun finnOverføringerMedStatusFeilet(): List<VedtakOverføring?>?
}
