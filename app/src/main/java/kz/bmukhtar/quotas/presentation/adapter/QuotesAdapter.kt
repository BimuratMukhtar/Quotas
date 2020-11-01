package kz.bmukhtar.quotas.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_quota.view.*
import kz.bmukhtar.quotas.R
import kz.bmukhtar.quotas.domain.model.Quote

class QuotesAdapter : ListAdapter<Quote, QuotesAdapter.QuoteVH>(
    QuoteDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuoteVH(LayoutInflater.from(parent.context).inflate(R.layout.item_quota, parent, false))

    override fun onBindViewHolder(holder: QuoteVH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuoteVH(view: View) : RecyclerView.ViewHolder(view) {

        private val ticker = view.ticker
        private val securityImage = view.security_image
        private val stockMarket = view.stock_market
        private val change = view.change
        private val changeInPercent = view.change_in_percent

        fun bind(quote: Quote) {
            ticker.text = quote.ticker
            stockMarket.text = quote.stockMarket
            change.text = quote.change.toString()
            changeInPercent.text = quote.changeInPercent.toString()
        }
    }
}

class QuoteDiffCallback : DiffUtil.ItemCallback<Quote>() {

    override fun areItemsTheSame(oldItem: Quote, newItem: Quote) = oldItem.ticker == newItem.ticker

    override fun areContentsTheSame(oldItem: Quote, newItem: Quote) = oldItem == newItem
}
