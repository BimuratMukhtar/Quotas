package kz.bmukhtar.quotas.data.model

sealed class QuoteChange(
    open val ticker: String
) {
    data class New(
        override val ticker: String,
        val change: Double,
        val stockMarket: String,
        val securityName: String,
        val price: Double,
        val priceDiff: Double,
        val minStep: Double
    ) : QuoteChange(ticker)

    data class Update(
        override val ticker: String,
        val change: Double
    ) : QuoteChange(ticker)
}