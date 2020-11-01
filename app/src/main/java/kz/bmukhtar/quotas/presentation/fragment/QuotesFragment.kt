package kz.bmukhtar.quotas.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kz.bmukhtar.quotas.R
import kz.bmukhtar.quotas.domain.repository.QuotasRepository
import kz.bmukhtar.quotas.presentation.viewmodel.QuotesViewModel
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance

class QuotesFragment : Fragment(R.layout.quotes_fragment), DIAware {

    override val di by di()

    private val repository: QuotasRepository by instance()
    private lateinit var viewModel: QuotesViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(QuotesViewModel::class.java)
        repository.subscribeToQuotas()
    }

}