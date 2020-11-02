package kz.bmukhtar.quotas.data.repository

import kz.bmukhtar.quotas.data.datasource.QuotesLocalDataSource
import kz.bmukhtar.quotas.data.datasource.QuotesRemoteDataSource
import kz.bmukhtar.quotas.data.model.QuoteChange
import kz.bmukhtar.quotas.domain.model.ChangeHistory
import kz.bmukhtar.quotas.domain.model.Quote
import kz.bmukhtar.quotas.domain.model.QuoteResult
import kz.bmukhtar.quotas.domain.model.observable.DefaultTypedObservable
import kz.bmukhtar.quotas.domain.model.observable.TypedObservable
import kz.bmukhtar.quotas.domain.model.observable.newTypedObserver
import kz.bmukhtar.quotas.domain.repository.QuotesRepository
import kotlin.math.roundToInt


class DefaultQuotesRepository(
    remoteDataSource: QuotesRemoteDataSource,
    private val localDataSource: QuotesLocalDataSource,
    private val expireScheduler: QuoteChangeExpireScheduler = QuoteChangeExpireScheduler()
) : QuotesRepository {

    private val quoteObservable = DefaultTypedObservable<QuoteResult>()

    init {
        remoteDataSource.subscribeToQuoteChanges().addObserver(newTypedObserver {
            handleQuotesChangeEvent(it)
        })
    }

    override fun subscribeToQuotes(): TypedObservable<QuoteResult> = quoteObservable

    private fun handleQuotesChangeEvent(changes: List<QuoteChange>) {
        changes.forEach { change ->
            when (change) {
                is QuoteChange.New -> handleNewQuote(change)
                is QuoteChange.Update -> handleUpdateQuote(change)
            }
        }
        notifyQuotesChange()
    }

    private fun handleUpdateQuote(
        change: QuoteChange.Update
    ) {
        val prevQuote = localDataSource.getQuoteOrNull(change.ticker) ?: return

        val changeHistory = ChangeHistory(
            prev = prevQuote.changeHistory.current,
            current = change.change
        )
        val updatedQuote = prevQuote.copy(
            changeHistory = changeHistory
        )
        localDataSource.updateQuote(updatedQuote)
        if (changeHistory.hasChange()) {
            expireScheduler.schedule(
                quote = updatedQuote,
                onQuoteUpdate = {
                    localDataSource.updateQuote(it)
                    notifyQuotesChange()
                }
            )
        }
    }

    private fun handleNewQuote(newQuote: QuoteChange.New) {
        val minStep = newQuote.minStep
        val change = newQuote.change

        localDataSource.updateQuote(quote = Quote(
            ticker = newQuote.ticker,
            stockMarket = newQuote.stockMarket,
            securityName = newQuote.securityName,
            price = getRoundedDouble(newQuote.price, minStep),
            changeHistory = ChangeHistory(prev = change, current = change),
            priceDiff = getRoundedDouble(newQuote.priceDiff, minStep)
        ))
    }

    private fun notifyQuotesChange() =
        quoteObservable.notifyObservers(QuoteResult(localDataSource.toList()))

    private fun getRoundedDouble(input: Double, roundTo: Double): Double {
        val decimals = (1 / roundTo).roundToInt()
        return (input * decimals) / decimals
    }
}
