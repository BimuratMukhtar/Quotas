package kz.bmukhtar.quotas.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import kz.bmukhtar.quotas.R
import kz.bmukhtar.quotas.domain.model.Quote
import kz.bmukhtar.quotas.domain.model.QuoteResult


class QuotesAdapter : RecyclerView.Adapter<QuoteVH>() {

    private val sortedQuotes: SortedList<Quote> =
        SortedList(Quote::class.java, SortedListCallback(this))

    override fun getItemCount(): Int = sortedQuotes.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuoteVH(LayoutInflater.from(parent.context).inflate(R.layout.item_quota, parent, false))

    override fun onBindViewHolder(holder: QuoteVH, position: Int) {
        holder.bind(sortedQuotes[position])
    }

    override fun onViewRecycled(holder: QuoteVH) {
        super.onViewRecycled(holder)
        holder.onViewRecycled()
    }

    fun handleQuotesUpdate(result: QuoteResult) {
        sortedQuotes.addAll(result.quotes)
    }
}
