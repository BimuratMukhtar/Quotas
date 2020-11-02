package kz.bmukhtar.quotas.data.repository

import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kz.bmukhtar.quotas.data.mapper.QuotesApiParser
import kz.bmukhtar.quotas.data.model.QuoteChange
import kz.bmukhtar.quotas.domain.model.ChangeHistory
import kz.bmukhtar.quotas.domain.model.Quote
import kz.bmukhtar.quotas.domain.model.QuoteResult
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
    private val quotesApiParser: QuotesApiParser,
    private val expireScheduler: QuoteChangeExpireScheduler = QuoteChangeExpireScheduler()
) : QuotasRepository {

    private val quoteObservable: TypedObservable<QuoteResult> = BaseTypedObservable()
    private val quotes = TreeSet<Quote>(compareBy { it.ticker })

    init {
        startListeningUpdates()
    }

    override fun subscribeToQuotas(): TypedObservable<QuoteResult> = quoteObservable

    private fun startListeningUpdates() {
        val socket = IO.socket(BASE_URL)

        socket.on(QUOTAS_CHANGE_EVENT) { data ->
            handleQuotesChangeEvent(data)
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

    private fun handleQuotesChangeEvent(data: Array<Any>) {
        val changes = quotesApiParser.parse(data)

        changes.forEach { change ->
            when (change) {
                is QuoteChange.New -> handleNewQuote(change)
                is QuoteChange.Update -> handleUpdateQuote(change)
            }
        }
        notifyQuotesChange()
    }

    private fun handleUpdateQuote(
        change: QuoteChange.Update
    ) {
        val prevQuote = quotes.find { it.ticker == change.ticker } ?: return
        val changeHistory = ChangeHistory(
            prev = prevQuote.changeHistory.current,
            current = change.change
        )
        val updatedQuote = prevQuote.copy(
            changeHistory = changeHistory
        )
        updateQuote(updatedQuote)
        if (changeHistory.hasChange()) {
            expireScheduler.schedule(
                quote = updatedQuote,
                onQuoteUpdate = {
                    updateQuote(it)
                    notifyQuotesChange()
                }
            )
        }
    }

    private fun handleNewQuote(newQuote: QuoteChange.New) {
        val minStep = newQuote.minStep
        val change = newQuote.change

        updateQuote(quote = Quote(
            ticker = newQuote.ticker,
            stockMarket = newQuote.stockMarket,
            securityName = newQuote.securityName,
            price = getRoundedDouble(newQuote.price, minStep),
            changeHistory = ChangeHistory(prev = change, current = change),
            priceDiff = getRoundedDouble(newQuote.priceDiff, minStep)
        ))
    }

    private fun updateQuote(quote: Quote) {
        runBlocking(Dispatchers.Main) {
            quotes.removeAll { it.ticker == quote.ticker }
            quotes.add(quote)
        }
    }

    private fun notifyQuotesChange() =
        quoteObservable.notifyObservers(QuoteResult(quotes.toList()))

    private fun getRoundedDouble(input: Double, roundTo: Double): Double {
        val decimals = (1 / roundTo).roundToInt()
        return (input * decimals) / decimals
    }
}
