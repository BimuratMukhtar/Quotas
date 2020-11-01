package kz.bmukhtar.quotas.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuotesApi(
    @SerialName("q")
    val quotes: List<QuoteApi> = listOf()
) {
    @Serializable
    data class QuoteApi(
        @SerialName("c")
        val ticker: String = "",
        @SerialName("pcp")
        val changeInPercent: Double = 0.0,
        @SerialName("ltr")
        val stockMarket: String = "",
        @SerialName("name")
        val securityName: String = "",
        @SerialName("ltp")
        val price: Double = 0.0,
        @SerialName("chg")
        val change: Double = 0.0,
        @SerialName("min_step")
        val minStep: Double = 0.0
    )
}