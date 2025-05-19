package no.nav.bidrag.admin.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.bidrag.admin.dto.LeggTilEndringsloggEndring
import no.nav.bidrag.admin.dto.OppdaterEndringsloggRequest
import no.nav.bidrag.admin.dto.OpprettEndringsloggRequest
import no.nav.bidrag.admin.persistence.entity.Endringslogg
import no.nav.bidrag.admin.persistence.entity.EndringsloggEndring
import no.nav.bidrag.admin.persistence.entity.EndringsloggTilhørerSkjermbilde
import no.nav.bidrag.admin.persistence.entity.LestAvBruker
import no.nav.bidrag.admin.persistence.entity.Person
import no.nav.bidrag.admin.persistence.repository.EndringsloggRepository
import no.nav.bidrag.admin.persistence.repository.LestAvBrukerRepository
import no.nav.bidrag.admin.persistence.repository.Personrepository
import no.nav.bidrag.admin.utils.hentBrukerIdent
import no.nav.bidrag.admin.utils.hentBrukerNavn
import no.nav.bidrag.admin.utils.ugyldigForespørsel
import no.nav.bidrag.commons.security.utils.TokenUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class EndringsloggService(
    private val endringsloggRepository: EndringsloggRepository,
    private val personrepository: Personrepository,
    private val lestAvBrukerRepository: LestAvBrukerRepository,
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
    fun hentAlleForType(type: List<EndringsloggTilhørerSkjermbilde>): List<Endringslogg> {
        val endringer = endringsloggRepository.findAllByTilhørerSkjermbilde(type.flatMap { it.tilTyper })
        log.info { "Hentet endringslogg for type $type: $endringer" }
        return endringer
    }

    fun hentEndringslogg(endringsloggId: Long): Endringslogg =
        endringsloggRepository
            .findById(
                endringsloggId,
            ).orElseThrow { ugyldigForespørsel("Endringslogg med id $endringsloggId finnes ikke") }

    fun oppdaterLestAvBruker(endringsloggId: Long): Endringslogg {
        log.info {
            "Oppdaterer lest av bruker for endringslogg $endringsloggId og saksbehandler ${TokenUtils.hentSaksbehandlerIdent()}"
        }
        val endringslogg = hentEndringslogg(endringsloggId)
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
        val lestAvBruker =
            lestAvBrukerRepository
                .findByPersonAndEndringslogg(person, endringslogg)
                ?: run {
                    val lestAvBrukerNy =
                        LestAvBruker(
                            person = person,
                            endringslogg = endringslogg,
                        )
                    endringslogg.brukerLesinger.add(lestAvBrukerRepository.save(lestAvBrukerNy))
                    endringsloggRepository.save(endringslogg)
                    lestAvBrukerNy
                }

        lestAvBruker.lestTidspunkt = LocalDateTime.now()
        log.info {
            "Oppdaterte lest av bruker for endringslogg $endringsloggId og saksbehandler ${TokenUtils.hentSaksbehandlerIdent()}"
        }
        return endringslogg
    }

    private fun Endringslogg.hentEndring(endringsloggEndringId: Long) =
        endringer.find { it.id == endringsloggEndringId }
            ?: ugyldigForespørsel("Endringslogg endring med id $endringsloggEndringId finnes ikke i endringslogg med id $id")

    @Transactional
    fun oppdaterEndringslogg(
        endringsloggId: Long,
        request: OppdaterEndringsloggRequest,
    ): Endringslogg {
        log.info { "Oppdaterer endringslogg $endringsloggId med $request" }
        val endringslogg = hentEndringslogg(endringsloggId)

        endringslogg.aktivFraTidspunkt = request.aktivFraTidspunkt ?: endringslogg.aktivFraTidspunkt
        endringslogg.aktivTilTidspunkt = request.aktivTilTidspunkt ?: endringslogg.aktivTilTidspunkt
        endringslogg.erPåkrevd = request.erPåkrevd ?: endringslogg.erPåkrevd
        endringslogg.tittel = request.tittel ?: endringslogg.tittel
        endringslogg.sammendrag = request.sammendrag ?: endringslogg.sammendrag
        endringslogg.endringstyper = request.endringstyper ?: endringslogg.endringstyper
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
        log.info { "Aktiverer enddringslogg $endringsloggId" }
        val endringslogg = hentEndringslogg(endringsloggId)

        endringslogg.aktivFraTidspunkt = LocalDate.now()
        endringslogg.aktivTilTidspunkt = null
        return endringslogg
    }

    @Transactional
    fun deaktiverEndringslogg(endringsloggId: Long): Endringslogg {
        log.info { "Deaktiverer enddringslogg $endringsloggId" }
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
                opprettetAv = hentBrukerIdent(),
                opprettetAvNavn = hentBrukerNavn(),
                endringstyper = request.endringstyper,
            )
        request.endringer?.forEachIndexed { index, endringRequest ->
            endringsLogg.endringer.add(
                EndringsloggEndring(
                    endringslogg = endringsLogg,
                    tittel = endringRequest.tittel,
                    innhold = endringRequest.innhold,
                    rekkefølgeIndeks = index,
                ),
            )
        }
        val endringslogg = endringsloggRepository.save(endringsLogg)
        log.info { "Opprettet enddringslogg $endringslogg" }
        return endringslogg
    }

    @Transactional
    fun oppdaterLestAvBrukerEndring(
        endringsloggId: Long,
        endringsloggEndringId: Long,
    ): Endringslogg {
        log.info {
            "Oppdaterer lest av bruker for endringslogg endring $endringsloggEndringId i endringslogg $endringsloggId og saksbehandler ${TokenUtils.hentSaksbehandlerIdent()}"
        }
        val endringslogg = hentEndringslogg(endringsloggId)
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
            oppdaterLestAvBruker(endringsloggId)
        }
        return endringslogg
    }

    @Transactional
    fun slettEndring(
        endringsloggId: Long,
        endringId: Long,
    ): Endringslogg {
        log.info { "Sletter endring $endringId fra endringslogg $endringsloggId" }

        val endringslogg = hentEndringslogg(endringsloggId)

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

        val endringslogg = hentEndringslogg(endringsloggId)

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
