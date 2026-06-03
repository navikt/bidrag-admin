package no.nav.bidrag.produksjonsoppfølging.jira.dto

data class OpprettSakRequest(
    val fields: MutableMap<String, Any> = mutableMapOf<String, Any>(),
)
