package kz.bmukhtar.quotas.domain.model

import kotlin.math.abs


data class Quote(
    val ticker: String,
    val changeHistory: ChangeHistory,
    val stockMarket: String,
    val securityName: String,
    val price: Double,
    val priceDiff: Double
)

private const val CHANGE_THRESHOLD = 0.0000001

data class ChangeHistory(
    val prev: Double,
    val current: Double
) {
    private val diff = current - prev

    fun isPositive() = diff > CHANGE_THRESHOLD

    fun isNegative() = diff < -CHANGE_THRESHOLD

    fun hasChange() = abs(diff) > CHANGE_THRESHOLD
}