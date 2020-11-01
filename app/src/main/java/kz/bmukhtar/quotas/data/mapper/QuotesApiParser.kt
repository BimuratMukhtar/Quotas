package kz.bmukhtar.quotas.data.mapper

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kz.bmukhtar.quotas.data.model.QuotesApi
import kz.bmukhtar.quotas.domain.model.Quote
import org.json.JSONObject

private const val QUOTES_POSITION = 0

class QuotesApiParser {

    private val format = Json { ignoreUnknownKeys = true }

    fun map(
        data: Array<Any>
    ): List<Quote> {
        val quotesAsString = (data[QUOTES_POSITION] as? JSONObject)?.toString()
            ?: return emptyList()
        val quotesApiResponse = format.decodeFromString<QuotesApi>(quotesAsString)
        return mapQuotesApi(quotesApiResponse)
    }

    private fun mapQuotesApi(quotesApi: QuotesApi): List<Quote> = quotesApi.quotes.map {
        mapQuoteApi(it)
    }

    private fun mapQuoteApi(quoteApi: QuotesApi.QuoteApi) = quoteApi.run {
        Quote(
            ticker = ticker,
            changeInPercent = changeInPercent,
            stockMarket = stockMarket,
            securityName = securityName,
            price = getRoundedDouble(price, minStep),
            change = getRoundedDouble(change, minStep),
            minStep = minStep
        )
    }

    private fun getRoundedDouble(input: Double, roundTo: Double): Double {
//        return round(input / roundTo) * roundTo
        return input
    }
}