package kz.bmukhtar.quotas.domain.repository

import kz.bmukhtar.quotas.domain.model.QuoteResult
import kz.bmukhtar.quotas.domain.model.observable.TypedObservable

interface QuotesRepository {

    fun subscribeToQuotes(): TypedObservable<QuoteResult>
}