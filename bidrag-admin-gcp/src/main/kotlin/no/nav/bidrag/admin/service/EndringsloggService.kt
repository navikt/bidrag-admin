package no.nav.bidrag.admin.service

import no.nav.bidrag.admin.dto.EndringsLoggDto
import no.nav.bidrag.admin.dto.LeggTilEndringsloggEndring
import no.nav.bidrag.admin.dto.OppdaterEndringsloggRequest
import no.nav.bidrag.admin.dto.OpprettEndringsloggRequest
import no.nav.bidrag.admin.dto.toDto
import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.admin.persistence.entity.EndringsloggEndring
import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import no.nav.bidrag.admin.persistence.entity.LestAvBruker
import no.nav.bidrag.admin.persistence.entity.Person
import no.nav.bidrag.admin.persistence.repository.EndringsloggRepository
import no.nav.bidrag.admin.persistence.repository.LestAvBrukerRepository
import no.nav.bidrag.admin.persistence.repository.Personrepository
import no.nav.bidrag.admin.utils.ugyldigForespørsel
import no.nav.bidrag.commons.security.utils.TokenUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class EndringsloggService(
    private val endringsloggRepository: EndringsloggRepository,
    private val personrepository: Personrepository,
    private val lestAvBrukerRepository: LestAvBrukerRepository,
) {
    @Transactional
    fun hentAlleForType(type: EndringsloggTilhørerSkjermbilde): List<EndringsLoggDto> =
        endringsloggRepository.findAllByTilhørerSkjermbilde(type).map { it.toDto() }

    fun hentEndringslogg(endringsloggId: Long): Endringslogg =
        endringsloggRepository
            .findById(
                endringsloggId,
            ).orElseThrow { IllegalArgumentException("Endringslogg med id $endringsloggId finnes ikke") }

    @Transactional
    fun oppdaterLestAvBruker(endringsloggId: Long) {
        val endringslogg =
            endringsloggRepository
                .findById(endringsloggId)
                .orElseThrow { IllegalArgumentException("Endringslogg med id $endringsloggId finnes ikke") }

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

        val lestAvBruker = lestAvBrukerRepository.findByPersonAndEndringslogg(person, endringslogg)
        if (lestAvBruker == null) {
            val lestAvBrukerNy =
                LestAvBruker(
                    person = person,
                    endringslogg = endringslogg,
                )
            endringslogg.brukerLesinger.add(lestAvBrukerRepository.save(lestAvBrukerNy))
            endringsloggRepository.save(endringslogg)
        }
    }

    @Transactional
    fun slettEndring(
        endringsloggId: Long,
        endringId: Long,
    ): Endringslogg {
        val endringslogg = hentEndringslogg(endringsloggId)

        val endring =
            endringslogg.endringer.find { it.id == endringId }
                ?: ugyldigForespørsel("Endringslogg endring med id $endringId finnes ikke i endringslogg med id $endringsloggId")
        endringslogg.endringer.remove(endring)
        return endringslogg
    }

    @Transactional
    fun leggTilEndringsloggEndring(
        endringsloggId: Long,
        request: LeggTilEndringsloggEndring,
    ): Endringslogg {
        val endringslogg =
            endringsloggRepository
                .findById(endringsloggId)
                .orElseThrow { IllegalArgumentException("Endringslogg med id $endringsloggId finnes ikke") }

        val sisteRekkefølgeIndeks = endringslogg.endringer.size
        endringslogg.endringer.add(
            EndringsloggEndring(
                innhold = request.innhold,
                tittel = request.tittel,
                endringslogg = endringslogg,
                rekkefølgeIndeks = sisteRekkefølgeIndeks + 1,
            ),
        )
        return endringslogg
    }

    @Transactional
    fun oppdaterEndringslogg(
        endringsloggId: Long,
        request: OppdaterEndringsloggRequest,
    ): Endringslogg {
        val endringslogg = hentEndringslogg(endringsloggId)

        endringslogg.aktivFraTidspunkt = request.aktivFraTidspunkt ?: endringslogg.aktivFraTidspunkt
        endringslogg.aktivTilTidspunkt = request.aktivTilTidspunkt ?: endringslogg.aktivTilTidspunkt
        endringslogg.erPåkrevd = request.erPåkrevd ?: endringslogg.erPåkrevd
        endringslogg.tittel = request.tittel ?: endringslogg.tittel
        endringslogg.sammendrag = request.sammendrag ?: endringslogg.sammendrag
        val endringerRequestIds = request.endringer?.map { it.id }?.toHashSet()
        if (endringerRequestIds != null && endringerRequestIds.size == endringslogg.endringer.size) {
            val nyeEndringer =
                request.endringer
                    .mapIndexed { index, endringRequest ->
                        val endring =
                            endringslogg.endringer.find { e -> e.id == endringRequest.id }
                                ?: ugyldigForespørsel("Endringslogg endring med id ${endringRequest.id} finnes ikke")
                        endring.tittel = endringRequest.tittel ?: endring.tittel
                        endring.innhold = endringRequest.innhold ?: endring.innhold
                        endring.rekkefølgeIndeks = index
                        endring
                    }
            endringslogg.endringer.clear()
            endringslogg.endringer.addAll(nyeEndringer)
        }
        return endringslogg
    }

    @Transactional
    fun aktiverEndringslogg(endringsloggId: Long): Endringslogg {
        val endringslogg = hentEndringslogg(endringsloggId)

        endringslogg.aktivFraTidspunkt = LocalDate.now()
        endringslogg.aktivTilTidspunkt = null
        return endringslogg
    }

    @Transactional
    fun deaktiverEndringslogg(endringsloggId: Long): Endringslogg {
        val endringslogg = hentEndringslogg(endringsloggId)

        endringslogg.aktivTilTidspunkt = LocalDate.now()
        return endringslogg
    }

    @Transactional
    fun opprettEndringslogg(request: OpprettEndringsloggRequest): Endringslogg {
        val endringsLogg =
            Endringslogg(
                tittel = request.tittel,
                tilhørerSkjermbilde = request.tilhørerSkjermbilde,
                sammendrag = request.sammendrag,
                erPåkrevd = request.erPåkrevd,
                aktivFraTidspunkt = request.aktivFraTidspunkt,
                aktivTilTidspunkt = request.aktivTilTidspunkt,
            )
        return endringsloggRepository.save(endringsLogg)
    }
}
