package kz.bmukhtar.quotas.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.quotes_fragment.*
import kz.bmukhtar.quotas.R
import kz.bmukhtar.quotas.presentation.adapter.QuotesAdapter
import kz.bmukhtar.quotas.presentation.viewmodel.DIViewModelFactory
import kz.bmukhtar.quotas.presentation.viewmodel.QuotesViewModel
import org.kodein.di.DIAware
import org.kodein.di.android.x.di

class QuotesFragment : Fragment(R.layout.quotes_fragment), DIAware {

    private lateinit var viewModel: QuotesViewModel
    private val recyclerView get() = quotes_recycler
    private val adapter by lazy { QuotesAdapter() }

    override val di by di()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initVM()
    }

    private fun initVM() {
        viewModel = ViewModelProvider(this, DIViewModelFactory(di)).get(QuotesViewModel::class.java)

        viewModel.quoteUpdate.observe(viewLifecycleOwner) {
            adapter.handleQuotesUpdate(it)
        }
    }

    private fun initViews() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

}