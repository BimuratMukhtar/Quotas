package kz.bmukhtar.quotas.domain.model

data class Quote(
    val ticker: String,
    val changeHistory: ChangeHistory,
    val stockMarket: String,
    val securityName: String,
    val price: Double,
    val priceDiff: Double
)

data class ChangeHistory(
    val prev: Double,
    val current: Double
) {
    private val diff = current - prev

    fun isPositive() = diff > 0

    fun isNegative() = diff < 0
}