package kz.bmukhtar.quotas.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kz.bmukhtar.quotas.domain.model.QuoteResult
import kz.bmukhtar.quotas.domain.model.observable.newTypedObserver
import kz.bmukhtar.quotas.domain.repository.QuotesRepository

class QuotesViewModel(
    repository: QuotesRepository
) : ViewModel() {

    private val _quotaChanges = MutableLiveData<QuoteResult>()

    private val observable = repository.subscribeToQuotes()
    private val observer = newTypedObserver<QuoteResult> {
        _quotaChanges.postValue(it)
    }

    init {
        observable.addObserver(observer)
    }

    val quoteResult: LiveData<QuoteResult> = _quotaChanges

    override fun onCleared() {
        super.onCleared()
        observable.deleteObserver(observer)
    }
}