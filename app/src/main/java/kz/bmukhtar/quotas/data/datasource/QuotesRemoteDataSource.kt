package kz.bmukhtar.quotas.data.datasource

import io.socket.client.IO
import io.socket.client.Socket
import kz.bmukhtar.quotas.data.mapper.QuotesApiParser
import kz.bmukhtar.quotas.data.model.QuoteChange
import kz.bmukhtar.quotas.domain.model.observable.DefaultTypedObservable
import kz.bmukhtar.quotas.domain.model.observable.TypedObservable
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

class QuotesRemoteDataSource(
    private val quotesApiParser: QuotesApiParser,
) {

    private val _quotesChangeObservable = DefaultTypedObservable<List<QuoteChange>>()

    init {
        startListeningUpdates()
    }

    fun subscribeToQuoteChanges(): TypedObservable<List<QuoteChange>> = _quotesChangeObservable

    private fun startListeningUpdates() {
        val socket = IO.socket(BASE_URL)

        socket.on(QUOTAS_CHANGE_EVENT) { data ->
            onQuotesChange(data)
        }.on(Socket.EVENT_CONNECT) {
            socket.emit(SUBSCRIBE_TO_QUOTAS_EVENT, getEmitParams())
        }
        socket.connect()
    }

    private fun getEmitParams(): JSONArray {
        val jsonArray = JSONArray()
        TICKER_TO_WATCH_CHANGES.forEach { jsonArray.put(it) }
        return jsonArray
    }

    private fun onQuotesChange(data: Array<Any>) {
        _quotesChangeObservable.notifyObservers(quotesApiParser.parse(data))
    }
}