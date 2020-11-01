package kz.bmukhtar.quotas.data.repository

import com.orhanobut.logger.Logger
import io.socket.client.IO
import io.socket.client.Socket
import kz.bmukhtar.quotas.data.mapper.QuotesApiMapper
import kz.bmukhtar.quotas.domain.model.QuoteResult
import kz.bmukhtar.quotas.domain.model.observable.BaseTypedObservable
import kz.bmukhtar.quotas.domain.model.observable.TypedObservable
import kz.bmukhtar.quotas.domain.repository.QuotasRepository
import org.json.JSONArray

private const val BASE_URL = "https://ws3.tradernet.ru/"
private const val SUBSCRIBE_TO_QUOTAS_EVENT = "sup_updateSecurities2"
private const val QUOTAS_CHANGE_EVENT = "q"
private val TICKER_TO_WATCH_CHANGES =
    arrayOf(
        "RSTI", "GAZP", "MRKZ", "RUAL", "HYDR", "MRKS", "SBER", "FEES", "TGKA", "VTBR",
        "ANH.US", "VICL.US", "BURG.US", "NBL.US", "YETI.US", "WSFS.US", "NIO.US", "DXC.US",
        "MIC.US", "HSBC.US", "EXPN.EU", "GSK.EU", "SHP.EU", "MAN.EU", "DB1.EU", "MUV2.EU",
        "TATE.EU", "KGF.EU", "MGGT.EU", "SGGD.EU"
    )

class DefaultQuotasRepository(
    private val quotesApiMapper: QuotesApiMapper
) : QuotasRepository {

    private val observable: TypedObservable<QuoteResult> = BaseTypedObservable()

    override fun subscribeToQuotas(): TypedObservable<QuoteResult> = observable

    override fun startUpdates() {
        startListeningUpdates()
    }

    private fun startListeningUpdates() {
        val socket = IO.socket(BASE_URL)

        socket.on(QUOTAS_CHANGE_EVENT) { data ->
            onQuotesUpdate(data)
        }.on(Socket.EVENT_CONNECT) {
            Logger.d("on connect")
            socket.emit(SUBSCRIBE_TO_QUOTAS_EVENT, getEmitParams())
        }.on(Socket.EVENT_DISCONNECT) {
            Logger.d("on disconnect")
        }
        socket.connect()
    }

    private fun getEmitParams(): JSONArray {
        val jsonArray = JSONArray()
        TICKER_TO_WATCH_CHANGES.forEach { jsonArray.put(it) }
        return jsonArray
    }

    private fun onQuotesUpdate(data: Array<Any>) {
        val quotes = quotesApiMapper.map(data)
        observable.notifyObservers(QuoteResult.Update(quotes))
    }

}