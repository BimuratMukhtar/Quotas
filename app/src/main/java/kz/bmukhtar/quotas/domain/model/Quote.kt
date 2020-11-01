package kz.bmukhtar.quotas.domain.model

data class Quote(
    val ticker: String,
    val securityLogoUrl: String,
    val changeInPercent: Double,
    val stockMarket: String,
    val securityName: String,
    val price: String,
    val change: Double
)