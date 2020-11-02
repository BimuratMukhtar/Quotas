package kz.bmukhtar.quotas.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.bmukhtar.quotas.domain.model.ChangeHistory
import kz.bmukhtar.quotas.domain.model.Quote
import java.util.concurrent.ConcurrentHashMap

class QuoteChangeExpireScheduler(
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    private val scheduledToExpireQuotes = ConcurrentHashMap<String, Pair<Quote, Job>>()

    fun schedule(quote: Quote, onQuoteUpdate: (quote: Quote) -> Unit) {
        cancelPreviousExpireJob(quote)
        val expireJob = getExpireJobForQuote(quote, onQuoteUpdate)
        scheduledToExpireQuotes[quote.ticker] = quote to expireJob
    }

    private fun getExpireJobForQuote(
        quote: Quote,
        onQuoteUpdate: (quote: Quote) -> Unit
    ): Job {
        return coroutineScope.launch(Dispatchers.Main) {
            delay(1000)
            val (quoteToExpire, _) = scheduledToExpireQuotes.remove(quote.ticker) ?: return@launch
            val latestChange = quoteToExpire.changeHistory.current

            onQuoteUpdate(quoteToExpire.copy(changeHistory = ChangeHistory(
                prev = latestChange,
                current = latestChange
            )))
        }
    }


    private fun cancelPreviousExpireJob(quote: Quote) {
        val (_, prevJob) = scheduledToExpireQuotes.remove(quote.ticker)
            ?: return
        prevJob.cancel()
    }

}