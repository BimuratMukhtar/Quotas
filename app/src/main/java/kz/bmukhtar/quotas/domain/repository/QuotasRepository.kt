package kz.bmukhtar.quotas.domain.repository

import kz.bmukhtar.quotas.domain.model.QuotaResult
import kz.bmukhtar.quotas.domain.model.observable.TypedObservable

interface QuotasRepository {

    fun subscribeToQuotas(): TypedObservable<QuotaResult>
}