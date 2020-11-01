package kz.bmukhtar.quotas.presentation.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import coil.load
import kotlinx.android.synthetic.main.item_quota.view.*
import kz.bmukhtar.quotas.R
import kz.bmukhtar.quotas.domain.model.Quote
import kz.bmukhtar.quotas.domain.model.QuoteUpdate
import java.util.Locale

private const val BASE_LOGO_URL = "https://tradernet.ru/logos/get-logo-by-ticker?ticker="

class QuotesAdapter : RecyclerView.Adapter<QuoteVH>() {

    private val sortedQuotes: SortedList<Quote> =
        SortedList(Quote::class.java, SortedListCallback(this))

    override fun getItemCount(): Int = sortedQuotes.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuoteVH(LayoutInflater.from(parent.context).inflate(R.layout.item_quota, parent, false))

    override fun onBindViewHolder(holder: QuoteVH, position: Int) {
        holder.bind(sortedQuotes[position])
    }

    fun handleQuotesUpdate(update: QuoteUpdate) = when (update) {
        is QuoteUpdate.Update -> {
            sortedQuotes.addAll(update.quotes)
        }
    }
}

class QuoteVH(view: View) : RecyclerView.ViewHolder(view) {

    private val ticker = view.ticker
    private val securityImage = view.security_image
    private val stockMarket = view.stock_market
    private val price = view.price
    private val change = view.change

    fun bind(quote: Quote) {
        ticker.text = quote.ticker
        bindChange(quote)
        bindStockMarket(quote)
        bindPrice(quote)
        bindLogo(quote)
    }

    private fun bindChange(quote: Quote) {
        val context = change.context
        change.background = getChangeBackground(quote, context)
        change.text = getChangeText(quote)
        change.setTextColor(getChangeColor(quote, context))
    }

    private fun getChangeText(quote: Quote): String {
        val currentChange = quote.changeHistory.current
        val formattedChangeText = "%.2f".format(currentChange)
        return if (currentChange < 0.0) {
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
            changeHistory.isNegative() || changeHistory.isPositive() -> {
                ContextCompat.getColor(context, R.color.white)
            }
            currentChange < 0 -> {
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
        val logoUrl = "$BASE_LOGO_URL${quote.ticker.toLowerCase(Locale.getDefault())}"
        securityImage.load(logoUrl)
    }
}
