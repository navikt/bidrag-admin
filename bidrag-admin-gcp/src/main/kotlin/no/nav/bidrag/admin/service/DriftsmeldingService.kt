package no.nav.bidrag.admin.service

import no.nav.bidrag.admin.dto.OppdaterDriftsmeldingRequest
import no.nav.bidrag.admin.dto.OpprettDriftsmeldingRequest
import no.nav.bidrag.admin.persistence.entity.Driftsmelding
import no.nav.bidrag.admin.persistence.entity.LestAvBruker
import no.nav.bidrag.admin.persistence.entity.Person
import no.nav.bidrag.admin.persistence.repository.DriftsmeldingRepository
import no.nav.bidrag.admin.persistence.repository.LestAvBrukerRepository
import no.nav.bidrag.admin.persistence.repository.Personrepository
import no.nav.bidrag.admin.utils.ugyldigForespørsel
import no.nav.bidrag.commons.security.utils.TokenUtils
import no.nav.bidrag.commons.service.organisasjon.SaksbehandlernavnProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class DriftsmeldingService(
    private val driftsmeldingRepository: DriftsmeldingRepository,
    private val personrepository: Personrepository,
    private val lestAvBrukerRepository: LestAvBrukerRepository,
) {
    @Transactional
    fun hentAlleAktiv(): List<Driftsmelding> = driftsmeldingRepository.hentAlleAktiv()

    fun hentDriftsmelding(driftsmeldingId: Long): Driftsmelding =
        driftsmeldingRepository
            .findById(
                driftsmeldingId,
            ).orElseThrow { ugyldigForespørsel("Driftsmelding med id $driftsmeldingId finnes ikke") }

    @Transactional
    fun oppdaterLestAvBruker(driftsmeldingId: Long) {
        val driftsmelding = hentDriftsmelding(driftsmeldingId)

        val person =
            personrepository.findByNavIdent(TokenUtils.hentSaksbehandlerIdent()!!)
                ?: run {
                    personrepository.save(
                        Person(
                            navIdent = TokenUtils.hentSaksbehandlerIdent()!!,
                            navn = TokenUtils.hentBruker() ?: "",
                        ),
                    )
                }

        val lestAvBruker = lestAvBrukerRepository.findByPersonAndDriftsmelding(person, driftsmelding)
        if (lestAvBruker == null) {
            val lestAvBrukerNy =
                LestAvBruker(
                    person = person,
                    driftsmelding = driftsmelding,
                )
            driftsmelding.brukerLesinger.add(lestAvBrukerRepository.save(lestAvBrukerNy))
            driftsmeldingRepository.save(driftsmelding)
        }
    }

    @Transactional
    fun oppdaterDriftsmelding(
        driftsmeldingId: Long,
        request: OppdaterDriftsmeldingRequest,
    ): Driftsmelding {
        val driftsmelding = hentDriftsmelding(driftsmeldingId)

        request.tittel?.let { driftsmelding.tittel = it }
        request.innhold?.let { driftsmelding.innhold = it }
        request.aktivFraTidspunkt?.let { driftsmelding.aktivFraTidspunkt = it }
        request.aktivTilTidspunkt?.let { driftsmelding.aktivTilTidspunkt = it }

        return driftsmelding
    }

    @Transactional
    fun opprettDriftsmelding(request: OpprettDriftsmeldingRequest): Driftsmelding {
        val driftsmelding =
            Driftsmelding(
                tittel = request.tittel,
                innhold = request.innhold,
                aktivFraTidspunkt = request.aktivFraTidspunkt,
                aktivTilTidspunkt = request.aktivTilTidspunkt,
                opprettetAv = TokenUtils.hentSaksbehandlerIdent() ?: TokenUtils.hentApplikasjonsnavn()!!,
                opprettetAvNavn =
                    TokenUtils.hentSaksbehandlerIdent()?.let { SaksbehandlernavnProvider.hentSaksbehandlernavn(it) }
                        ?: TokenUtils.hentApplikasjonsnavn()!!,
            )
        return driftsmeldingRepository.save(driftsmelding)
    }

    @Transactional
    fun aktiverDriftsmelding(driftsmeldingId: Long): Driftsmelding {
        val driftsmelding = hentDriftsmelding(driftsmeldingId)

        driftsmelding.aktivFraTidspunkt = LocalDate.now()
        driftsmelding.aktivTilTidspunkt = null
        return driftsmelding
    }

    @Transactional
    fun deaktiverDriftsmelding(driftsmeldingId: Long): Driftsmelding {
        val driftsmelding = hentDriftsmelding(driftsmeldingId)

        driftsmelding.aktivTilTidspunkt = LocalDate.now()
        return driftsmelding
    }
}
