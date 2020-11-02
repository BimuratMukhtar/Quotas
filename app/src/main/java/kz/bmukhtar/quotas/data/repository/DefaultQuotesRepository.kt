package kz.bmukhtar.quotas.data.repository

import io.socket.client.IO
import io.socket.client.Socket
import kz.bmukhtar.quotas.data.QuotesDataSource
import kz.bmukhtar.quotas.data.mapper.QuotesApiParser
import kz.bmukhtar.quotas.data.model.QuoteChange
import kz.bmukhtar.quotas.domain.model.ChangeHistory
import kz.bmukhtar.quotas.domain.model.Quote
import kz.bmukhtar.quotas.domain.model.QuoteResult
import kz.bmukhtar.quotas.domain.model.observable.BaseTypedObservable
import kz.bmukhtar.quotas.domain.model.observable.TypedObservable
import kz.bmukhtar.quotas.domain.repository.QuotasRepository
import org.json.JSONArray
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
    private val dataSource: QuotesDataSource,
    private val expireScheduler: QuoteChangeExpireScheduler = QuoteChangeExpireScheduler()
) : QuotasRepository {

    private val quoteObservable: TypedObservable<QuoteResult> = BaseTypedObservable()

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
        val prevQuote = dataSource.getQuoteOrNull(change.ticker) ?: return

        val changeHistory = ChangeHistory(
            prev = prevQuote.changeHistory.current,
            current = change.change
        )
        val updatedQuote = prevQuote.copy(
            changeHistory = changeHistory
        )
        dataSource.updateQuote(updatedQuote)
        if (changeHistory.hasChange()) {
            expireScheduler.schedule(
                quote = updatedQuote,
                onQuoteUpdate = {
                    dataSource.updateQuote(it)
                    notifyQuotesChange()
                }
            )
        }
    }

    private fun handleNewQuote(newQuote: QuoteChange.New) {
        val minStep = newQuote.minStep
        val change = newQuote.change

        dataSource.updateQuote(quote = Quote(
            ticker = newQuote.ticker,
            stockMarket = newQuote.stockMarket,
            securityName = newQuote.securityName,
            price = getRoundedDouble(newQuote.price, minStep),
            changeHistory = ChangeHistory(prev = change, current = change),
            priceDiff = getRoundedDouble(newQuote.priceDiff, minStep)
        ))
    }

    private fun notifyQuotesChange() =
        quoteObservable.notifyObservers(QuoteResult(dataSource.toList()))

    private fun getRoundedDouble(input: Double, roundTo: Double): Double {
        val decimals = (1 / roundTo).roundToInt()
        return (input * decimals) / decimals
    }
}
