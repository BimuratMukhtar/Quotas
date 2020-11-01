package kz.bmukhtar.quotas.data.repository

import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.orhanobut.logger.Logger
import kz.bmukhtar.quotas.domain.model.QuoteResult
import kz.bmukhtar.quotas.domain.model.observable.BaseTypedObservable
import kz.bmukhtar.quotas.domain.model.observable.TypedObservable
import kz.bmukhtar.quotas.domain.repository.QuotasRepository

private const val BASE_URL = "https://ws.tradernet.ru"
private const val QUOTAS_EVENT_EMIT = "sup_updateSecurities2"
private const val QUOTAS_EVENT = "q"
private val TICKER_TO_WATCH_CHANGES =
    arrayOf(
        "RSTI", "GAZP", "MRKZ"
    )

class DefaultQuotasRepository : QuotasRepository {

    private val observable: TypedObservable<QuoteResult> = BaseTypedObservable()

    init {
        initSocketIo()
    }

    override fun subscribeToQuotas(): TypedObservable<QuoteResult> = observable

    private fun initSocketIo() {
        val socket = IO.socket(BASE_URL)

        socket.on(QUOTAS_EVENT) {
            Logger.d("on quota event, args: $it")
        }.on(Socket.EVENT_CONNECT) {
            Logger.d("on connect")
        }.on(Socket.EVENT_ERROR) {
            Logger.d("on error: $it")
        }.on(Socket.EVENT_DISCONNECT) {
            Logger.d("on disconnect")
        }
        socket.connect().emit(QUOTAS_EVENT_EMIT, *TICKER_TO_WATCH_CHANGES)
    }
}