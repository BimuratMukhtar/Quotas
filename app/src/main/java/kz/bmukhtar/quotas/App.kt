package kz.bmukhtar.quotas

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kz.bmukhtar.quotas.data.datasource.QuotesLocalDataSource
import kz.bmukhtar.quotas.data.datasource.QuotesRemoteDataSource
import kz.bmukhtar.quotas.data.mapper.QuotesApiParser
import kz.bmukhtar.quotas.data.repository.DefaultQuotesRepository
import kz.bmukhtar.quotas.domain.repository.QuotesRepository
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
        bind<QuotesViewModel>() with provider { QuotesViewModel(repository = instance()) }
        bind<QuotesRepository>() with provider {
            DefaultQuotesRepository(
                remoteDataSource = instance(),
                localDataSource = instance()
            )
        }
        bind<QuotesApiParser>() with provider { QuotesApiParser() }
        bind<QuotesLocalDataSource>() with singleton { QuotesLocalDataSource() }
        bind<QuotesRemoteDataSource>() with singleton {
            QuotesRemoteDataSource(
                quotesApiParser = instance()
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
    }
}