package no.nav.bidrag.admin.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.bidrag.admin.dto.LeggTilEndringsloggEndring
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
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class EndringsloggEndringService(
    private val endringsloggRepository: EndringsloggRepository,
    private val personrepository: Personrepository,
    private val lestAvBrukerRepository: LestAvBrukerRepository,
    private val endringsloggService: EndringsloggService,
) {
    val EndringsloggTilhørerSkjermbilde?.tilTyper
        get() =
            when (this) {
                EndringsloggTilhørerSkjermbilde.BEHANDLING_ALLE ->
                    listOf(
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_BIDRAG,
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_FORSKUDD,
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_SÆRBIDRAG,
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_ALLE,
                    )
                EndringsloggTilhørerSkjermbilde.BEHANDLING_SÆRBIDRAG ->
                    listOf(
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_SÆRBIDRAG,
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_ALLE,
                    )
                EndringsloggTilhørerSkjermbilde.BEHANDLING_FORSKUDD ->
                    listOf(
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_FORSKUDD,
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_ALLE,
                    )
                EndringsloggTilhørerSkjermbilde.BEHANDLING_BIDRAG ->
                    listOf(
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_BIDRAG,
                        EndringsloggTilhørerSkjermbilde.BEHANDLING_ALLE,
                    )
                null -> emptyList()
                else -> listOf(this)
            }

    @Transactional
    fun oppdaterLestAvBrukerEndring(
        endringsloggId: Long,
        endringsloggEndringId: Long,
    ) {
        log.info {
            "Oppdaterer lest av bruker for endringslogg endring $endringsloggEndringId i endringslogg $endringsloggId og saksbehandler ${TokenUtils.hentSaksbehandlerIdent()}"
        }
        val endringslogg = endringsloggService.hentEndringslogg(endringsloggId)
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

        val endringsloggEndring = endringslogg.hentEndring(endringsloggEndringId)
        val lestAvBruker =
            lestAvBrukerRepository
                .findByPersonAndEndringsloggEndring(person, endringsloggEndring)
                ?: run {
                    val lestAvBrukerNy =
                        LestAvBruker(
                            person = person,
                            endringsloggEndring = endringsloggEndring,
                        )
                    endringsloggEndring.brukerLesinger.add(lestAvBrukerRepository.save(lestAvBrukerNy))
                    endringsloggRepository.save(endringslogg)
                    lestAvBrukerNy
                }

        lestAvBruker.lestTidspunkt = LocalDateTime.now()
        log.info {
            "Oppdaterte lest av bruker for endringslogg endring ${endringsloggEndring.id} i endringslogg $endringsloggId og saksbehandler ${TokenUtils.hentSaksbehandlerIdent()}"
        }

        if (endringslogg.erAlleLestAvBruker) {
            endringsloggService.oppdaterLestAvBruker(endringsloggId)
        }
    }

    private fun Endringslogg.hentEndring(endringsloggEndringId: Long) =
        endringer.find { it.id == endringsloggEndringId }
            ?: ugyldigForespørsel("Endringslogg endring med id $endringsloggEndringId finnes ikke i endringslogg med id $id")

    @Transactional
    fun slettEndring(
        endringsloggId: Long,
        endringId: Long,
    ): Endringslogg {
        log.info { "Sletter endring $endringId fra endringslogg $endringsloggId" }

        val endringslogg = endringsloggService.hentEndringslogg(endringsloggId)

        if (endringslogg.endringer.size == 1) ugyldigForespørsel("Kan ikke slette eneste endring i endringslogg $endringsloggId")

        val endring =
            endringslogg.endringer.find { it.id == endringId }
                ?: ugyldigForespørsel("Endringslogg endring med id $endringId finnes ikke i endringslogg med id $endringsloggId")
        endringslogg.endringer.remove(endring)
        synkroniserRekkefølgeIndeks(endringslogg)
        return endringslogg
    }

    private fun synkroniserRekkefølgeIndeks(endringslogg: Endringslogg) {
        endringslogg.endringer
            .sortedBy { it.rekkefølgeIndeks }
            .forEachIndexed { index, endringsloggEndring ->
                endringsloggEndring.rekkefølgeIndeks = index
            }
    }

    @Transactional
    fun leggTilEndringsloggEndring(
        endringsloggId: Long,
        request: LeggTilEndringsloggEndring,
    ): Endringslogg {
        log.info { "Legger til endring $request i endringslogg $endringsloggId" }

        val endringslogg = endringsloggService.hentEndringslogg(endringsloggId)

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
}
