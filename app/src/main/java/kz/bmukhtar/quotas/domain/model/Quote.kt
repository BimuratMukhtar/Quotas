package kz.bmukhtar.quotas.domain.model

data class Quote(
    val ticker: String,
    val changeInPercent: Double,
    val stockMarket: String,
    val securityName: String,
    val price: Double,
    val change: Double,
    val minStep: Double
)