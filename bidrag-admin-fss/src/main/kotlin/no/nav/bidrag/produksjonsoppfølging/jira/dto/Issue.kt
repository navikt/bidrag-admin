package no.nav.bidrag.produksjonsoppfølging.jira.dto

data class Issue(
    var id: String? = null,
    var self: String? = null,
    var key: String? = null,
    val fields: Fields = Fields(),
)
