package kz.bmukhtar.quotas.domain.model

sealed class QuoteUpdate {
    data class Update(val quotes: List<Quote>) : QuoteUpdate()
}