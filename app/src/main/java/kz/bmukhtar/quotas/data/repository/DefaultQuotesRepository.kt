package kz.bmukhtar.quotas.data.repository

import com.orhanobut.logger.Logger
import io.socket.client.IO
import io.socket.client.Socket
import kz.bmukhtar.quotas.data.mapper.QuotesApiParser
import kz.bmukhtar.quotas.data.model.QuoteChange
import kz.bmukhtar.quotas.domain.model.ChangeHistory
import kz.bmukhtar.quotas.domain.model.Quote
import kz.bmukhtar.quotas.domain.model.QuoteUpdate
import kz.bmukhtar.quotas.domain.model.observable.BaseTypedObservable
import kz.bmukhtar.quotas.domain.model.observable.TypedObservable
import kz.bmukhtar.quotas.domain.repository.QuotasRepository
import org.json.JSONArray
import java.util.TreeSet
import kotlin.math.roundToInt

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
    private val quotesApiParser: QuotesApiParser
) : QuotasRepository {

    private val observable: TypedObservable<QuoteUpdate> = BaseTypedObservable()
    private val quotes = TreeSet<Quote>(compareBy { it.ticker })

    init {
        startListeningUpdates()
    }

    override fun subscribeToQuotas(): TypedObservable<QuoteUpdate> = observable

    private fun startListeningUpdates() {
        val socket = IO.socket(BASE_URL)

        socket.on(QUOTAS_CHANGE_EVENT) { data ->
            handleQuotesChangeEvent(data)
        }.on(Socket.EVENT_CONNECT) {
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

    private fun handleQuotesChangeEvent(data: Array<Any>) {
        val changes = quotesApiParser.parse(data)

        changes.forEach { change ->
            when (change) {
                is QuoteChange.New -> addQuote(change)
                is QuoteChange.Update -> updateQuote(change)
            }
        }

        observable.notifyObservers(QuoteUpdate.Update(quotes.toList()))
        Logger.d(changes.toString())
    }

    private fun updateQuote(
        change: QuoteChange.Update
    ) {
        val prevQuote = quotes.find { it.ticker == change.ticker } ?: return
        quotes.remove(prevQuote)
        quotes.add(prevQuote.copy(
            changeHistory = ChangeHistory(prev = prevQuote.priceDiff, current = change.change)
        ))
    }

    private fun addQuote(newQuote: QuoteChange.New) {
        val prevQuote = quotes.find { it.ticker == newQuote.ticker }
        if (prevQuote != null) quotes.remove(prevQuote)

        val minStep = newQuote.minStep
        val change = newQuote.change

        quotes.add(Quote(
            ticker = newQuote.ticker,
            stockMarket = newQuote.stockMarket,
            securityName = newQuote.securityName,
            price = getRoundedDouble(newQuote.price, minStep),
            changeHistory = ChangeHistory(prev = change, current = change),
            priceDiff = getRoundedDouble(newQuote.priceDiff, minStep)
        ))
    }

    private fun getRoundedDouble(input: Double, roundTo: Double): Double {
        return (input / roundTo).roundToInt() * roundTo
    }

}