package kz.bmukhtar.quotas.data

import kz.bmukhtar.quotas.domain.model.Quote
import java.util.concurrent.ConcurrentHashMap

class QuotesDataSource {

    private val quotes = ConcurrentHashMap<String, Quote>()

    fun getQuoteOrNull(ticker: String) = quotes[ticker]

    fun updateQuote(quote: Quote) {
        quotes[quote.ticker] = quote
    }

    fun toList() = quotes.values.toList()

}