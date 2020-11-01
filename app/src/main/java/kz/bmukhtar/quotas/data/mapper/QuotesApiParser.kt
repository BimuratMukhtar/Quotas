package kz.bmukhtar.quotas.data.mapper

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kz.bmukhtar.quotas.data.model.QuoteChange
import kz.bmukhtar.quotas.data.model.QuotesApi
import org.json.JSONObject

private const val QUOTES_POSITION = 0

class QuotesApiParser {

    private val format = Json { ignoreUnknownKeys = true }

    fun parse(
        data: Array<Any>
    ): List<QuoteChange> {
        val quotesAsString = (data[QUOTES_POSITION] as? JSONObject)?.toString()
            ?: return emptyList()
        val quotesApiResponse = format.decodeFromString<QuotesApi>(quotesAsString)
        return quotesApiResponse.quotes.map {
            if (it.stockMarket == null ||
                it.securityName == null ||
                it.minStep == null ||
                it.price == null ||
                it.priceDiff == null) {
                QuoteChange.Update(ticker = it.ticker, change = it.change)
            } else {
                QuoteChange.New(
                    ticker = it.ticker,
                    change = it.change,
                    stockMarket = it.stockMarket,
                    securityName = it.securityName,
                    price = it.price,
                    priceDiff = it.priceDiff,
                    minStep = it.minStep
                )
            }
        }
    }

}