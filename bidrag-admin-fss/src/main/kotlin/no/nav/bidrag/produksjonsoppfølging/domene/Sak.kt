package no.nav.bidrag.produksjonsoppfølging.domene

import no.nav.bidrag.produksjonsoppfølging.utils.Entities

data class Sak(
    val saksnr: String,
    val roller: Entities<Rolle, Long>,
    val søknadslinjer: Entities<Søknadslinje, Long>,
    val beløp: Entities<Beløp, Long>,
)
