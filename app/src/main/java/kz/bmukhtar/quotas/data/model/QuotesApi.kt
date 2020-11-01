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
        val change: Double = 0.0,
        @SerialName("ltr")
        val stockMarket: String? = null,
        @SerialName("name")
        val securityName: String? = null,
        @SerialName("ltp")
        val price: Double? = null,
        @SerialName("chg")
        val priceDiff: Double? = null,
        @SerialName("min_step")
        val minStep: Double? = null
    )
}