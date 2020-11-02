package kz.bmukhtar.quotas.data.datasource

import kz.bmukhtar.quotas.domain.model.Quote
import java.util.concurrent.ConcurrentHashMap

class QuotesLocalDataSource {

    private val quotes = ConcurrentHashMap<String, Quote>()

    fun getQuoteOrNull(ticker: String) = quotes[ticker]

    fun updateQuote(quote: Quote) {
        quotes[quote.ticker] = quote
    }

    fun toList() = quotes.values.toList()

}