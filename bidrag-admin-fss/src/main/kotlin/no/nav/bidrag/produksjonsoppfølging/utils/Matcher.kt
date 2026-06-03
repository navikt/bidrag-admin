package no.nav.bidrag.produksjonsoppfølging.utils

fun interface Matcher<in T> {
    fun matches(value: T): Boolean
}
