package kz.bmukhtar.quotas.presentation.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import coil.size.Scale
import kotlinx.android.synthetic.main.item_quota.view.*
import kz.bmukhtar.quotas.R
import kz.bmukhtar.quotas.domain.model.Quote
import java.util.Locale

private const val BASE_LOGO_URL = "https://tradernet.ru/logos/get-logo-by-ticker?ticker="

class QuoteVH(view: View) : RecyclerView.ViewHolder(view) {

    private val ticker = view.ticker
    private val securityImage = view.security_image
    private val stockMarket = view.stock_market
    private val price = view.price
    private val change = view.change
    private val customImageViewTarget = CustomImageViewTarget(securityImage, ticker)

    private var imageDisposable: Disposable? = null

    fun bind(quote: Quote) {
        ticker.text = quote.ticker
        bindChange(quote)
        bindStockMarket(quote)
        bindPrice(quote)
        bindLogo(quote)
    }

    fun onViewRecycled() {
        imageDisposable?.dispose()
        imageDisposable = null
    }

    private fun bindChange(quote: Quote) {
        val context = change.context
        change.background = getChangeBackground(quote, context)
        change.text = getChangeText(quote)
        change.setTextColor(getChangeColor(quote, context))
    }

    private fun getChangeText(quote: Quote): String {
        val currentChange = quote.changeHistory.current
        val formattedChangeText = "${"%.2f".format(currentChange)}%"
        return if (currentChange <= 0.0) {
            formattedChangeText
        } else {
            "+$formattedChangeText"
        }
    }

    private fun getChangeColor(
        quote: Quote, context: Context
    ): Int {
        val changeHistory = quote.changeHistory
        val currentChange = changeHistory.current
        return when {
            changeHistory.hasChange() -> {
                ContextCompat.getColor(context, R.color.white)
            }
            currentChange == 0.0 -> {
                ContextCompat.getColor(context, R.color.black)
            }
            currentChange < 0.0 -> {
                ContextCompat.getColor(context, R.color.trade_red)
            }
            else -> {
                ContextCompat.getColor(context, R.color.trade_green)
            }
        }
    }

    private fun getChangeBackground(
        quote: Quote, context: Context
    ): Drawable? {
        val changeHistory = quote.changeHistory
        return when {
            changeHistory.isNegative() -> {
                ContextCompat.getDrawable(context, R.drawable.bg_change_in_percent_red)
            }
            changeHistory.isPositive() -> {
                ContextCompat.getDrawable(context, R.drawable.bg_change_in_percent_green)
            }
            else -> {
                null
            }
        }
    }

    private fun bindStockMarket(quote: Quote) {
        val stockMarketText = "${quote.stockMarket} | ${quote.securityName}"
        stockMarket.text = stockMarketText
    }

    private fun bindPrice(quote: Quote) {
        val priceText = "${quote.price} (${quote.priceDiff})"
        price.text = priceText
    }

    private fun bindLogo(quote: Quote) {
        val context = securityImage.context ?: return
        val logoUrl = "$BASE_LOGO_URL${quote.ticker.toLowerCase(Locale.getDefault())}"

        val imageRequest = ImageRequest.Builder(context)
            .data(logoUrl)
            .scale(Scale.FIT)
            .target(customImageViewTarget)
            .build()
        val imageLoader = context.imageLoader
        imageDisposable = imageLoader.enqueue(imageRequest)
    }
}