package kz.bmukhtar.quotas.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedListAdapterCallback
import kz.bmukhtar.quotas.domain.model.Quote

class SortedListCallback(
    adapter: RecyclerView.Adapter<*>
) : SortedListAdapterCallback<Quote>(adapter) {

    override fun compare(o1: Quote?, o2: Quote?): Int {
        if (o2 == null) return 1
        if (o1 == null) return -1
        return o1.ticker.compareTo(o2.ticker)
    }

    override fun areContentsTheSame(oldItem: Quote?, newItem: Quote?): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(item1: Quote?, item2: Quote?): Boolean {
        return item1?.ticker == item2?.ticker
    }
}