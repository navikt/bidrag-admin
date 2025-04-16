package no.nav.bidrag.admin.service

import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import no.nav.bidrag.admin.persistence.repository.EndringsloggRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EndringsloggService(
    private val endringsloggRepository: EndringsloggRepository,
) {
    @Transactional
    fun hentAlleForType(type: EndringsloggTilhørerSkjermbilde): List<Endringslogg> =
        endringsloggRepository.findALlByTilhørerSkjermbilde(type)
}
