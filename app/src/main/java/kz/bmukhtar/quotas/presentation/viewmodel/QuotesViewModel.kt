package kz.bmukhtar.quotas.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kz.bmukhtar.quotas.domain.model.QuoteUpdate
import kz.bmukhtar.quotas.domain.model.observable.newTypedObserver
import kz.bmukhtar.quotas.domain.repository.QuotasRepository

class QuotesViewModel(
    repository: QuotasRepository
) : ViewModel() {

    private val _quotaChanges = MutableLiveData<QuoteUpdate>()

    private val observable = repository.subscribeToQuotas()
    private val observer = newTypedObserver<QuoteUpdate> {
        _quotaChanges.postValue(it)
    }

    init {
        observable.addObserver(observer)
        repository.startUpdates()
    }

    val quoteUpdate: LiveData<QuoteUpdate> = _quotaChanges

    override fun onCleared() {
        super.onCleared()
        observable.deleteObserver(observer)
    }
}