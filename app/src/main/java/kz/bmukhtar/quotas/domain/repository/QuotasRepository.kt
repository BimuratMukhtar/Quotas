package kz.bmukhtar.quotas.domain.repository

import kz.bmukhtar.quotas.domain.model.QuoteResult
import kz.bmukhtar.quotas.domain.model.observable.TypedObservable

interface QuotasRepository {

    fun subscribeToQuotas(): TypedObservable<QuoteResult>
}