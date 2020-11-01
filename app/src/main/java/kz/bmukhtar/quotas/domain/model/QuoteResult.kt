package kz.bmukhtar.quotas.domain.model

sealed class QuoteResult {
    data class Update(val quotes: List<Quote>) : QuoteResult()
}