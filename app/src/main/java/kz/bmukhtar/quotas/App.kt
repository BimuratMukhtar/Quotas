package kz.bmukhtar.quotas

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kz.bmukhtar.quotas.data.QuotesDataSource
import kz.bmukhtar.quotas.data.mapper.QuotesApiParser
import kz.bmukhtar.quotas.data.repository.DefaultQuotasRepository
import kz.bmukhtar.quotas.domain.repository.QuotasRepository
import kz.bmukhtar.quotas.presentation.viewmodel.QuotesViewModel
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton


class App : Application(), DIAware {

    override val di = DI.lazy {
        import(androidXModule(this@App))
        bind<QuotasRepository>() with provider {
            DefaultQuotasRepository(
                quotesApiParser = instance(),
                dataSource = instance()
            )
        }
        bind<QuotesViewModel>() with provider { QuotesViewModel(repository = instance()) }
        bind<QuotesApiParser>() with provider { QuotesApiParser() }
        bind<QuotesDataSource>() with singleton { QuotesDataSource() }
    }

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
    }
}