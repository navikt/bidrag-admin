package no.nav.bidrag.produksjonsoppfølging.jira.dto

data class User(
    var name: String? = null,
) {
    companion object {
        fun withName(name: String) = User(name = name)
    }
}
