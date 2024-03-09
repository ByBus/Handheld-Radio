package host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen

import host.capitalquiz.arduinobluetoothcommander.domain.MessageMapper
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject


class MessageToUiMapper @Inject constructor(
    todayTranslation: String,
    yesterdayTranslation: String,
    private val timeProvider: CurrentTimeProvider,
) : MessageMapper<MessageUi> {
    private val fullFormat = "dd.MM.yyyy HH:mm"
    private val dayFormat = "EEEE hh:mm"
    private val yesterdayFormat = "'$yesterdayTranslation' HH:mm"
    private val todayFormat = "'$todayTranslation' HH:mm"
    private val simpleDateFormat = SimpleDateFormat("", Locale.getDefault())

    override fun invoke(
        id: Long,
        name: String,
        text: String,
        date: Long,
        chatId: Long,
        fromMe: Boolean,
    ): MessageUi {
        val pattern = when (daysBetweenDateAndNow(date)) {
            0 -> todayFormat
            1 -> yesterdayFormat
            in 2..6 -> dayFormat
            else -> fullFormat
        }
        simpleDateFormat.applyPattern(pattern)
        return MessageUi(id, name, text, simpleDateFormat.format(date), chatId, fromMe)
    }

    private fun daysBetweenDateAndNow(dateMillis: Long): Int {
        val now = timeProvider.now().toLocalDate()
        val date = dateMillis.toLocalDate()
        return date.until(now, ChronoUnit.DAYS).toInt()
    }

    private fun Long.toLocalDate() =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}