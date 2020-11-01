package kz.bmukhtar.quotas.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import coil.load
import kotlinx.android.synthetic.main.item_quota.view.*
import kz.bmukhtar.quotas.R
import kz.bmukhtar.quotas.domain.model.Quote
import kz.bmukhtar.quotas.domain.model.QuoteUpdate
import java.util.Locale

private const val BASE_LOGO_URL = "https://tradernet.ru/logos/get-logo-by-ticker?ticker="

class QuotesAdapter : RecyclerView.Adapter<QuotesAdapter.QuoteVH>() {

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

    inner class QuoteVH(view: View) : RecyclerView.ViewHolder(view) {

        private val ticker = view.ticker
        private val securityImage = view.security_image
        private val stockMarket = view.stock_market
        private val price = view.price
        private val changeInPercent = view.change_in_percent

        fun bind(quote: Quote) {
            ticker.text = quote.ticker
            bindChangeInPercent(quote)
            bindStockMarket(quote)
            bindPrice(quote)
            bindLogo(quote)
        }

        private fun bindChangeInPercent(quote: Quote) {
            val context = changeInPercent.context
            changeInPercent.background = if (quote.changeInPercent < 0.0) {
                ContextCompat.getDrawable(context, R.drawable.bg_change_in_percent_red)
            } else {
                ContextCompat.getDrawable(context, R.drawable.bg_change_in_percent_green)
            }
            changeInPercent.text = quote.changeInPercent.toString()
        }

        private fun bindStockMarket(quote: Quote) {
            val stockMarketText = "${quote.stockMarket} | ${quote.securityName}"
            stockMarket.text = stockMarketText
        }

        private fun bindPrice(quote: Quote) {
            val priceText = "${quote.price} (${quote.change})"
            price.text = priceText
        }

        private fun bindLogo(quote: Quote) {
            val logoUrl = "$BASE_LOGO_URL${quote.ticker.toLowerCase(Locale.getDefault())}"
            securityImage.load(logoUrl)
        }
    }
}

class QuoteDiffCallback : DiffUtil.ItemCallback<Quote>() {

    override fun areItemsTheSame(oldItem: Quote, newItem: Quote) = oldItem.ticker == newItem.ticker

    override fun areContentsTheSame(oldItem: Quote, newItem: Quote) = oldItem == newItem
}
