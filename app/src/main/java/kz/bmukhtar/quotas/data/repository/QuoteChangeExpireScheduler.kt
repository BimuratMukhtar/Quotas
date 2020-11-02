package kz.bmukhtar.quotas.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.bmukhtar.quotas.domain.model.ChangeHistory
import kz.bmukhtar.quotas.domain.model.Quote

class QuoteChangeExpireScheduler(
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    fun schedule(quote: Quote, onQuoteUpdate: (quote: Quote) -> Unit) {
        coroutineScope.launch {
            delay(1000)
            val latestChange = quote.changeHistory.current
            onQuoteUpdate(quote.copy(changeHistory = ChangeHistory(
                prev = latestChange,
                current = latestChange
            )))
        }
    }

}