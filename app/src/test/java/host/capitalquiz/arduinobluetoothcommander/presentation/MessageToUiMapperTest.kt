package host.capitalquiz.arduinobluetoothcommander.presentation

import host.capitalquiz.arduinobluetoothcommander.domain.Message
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.CurrentTimeProvider
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.MessageToUiMapper
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.MessageUi
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDateTime
import java.time.ZoneId

class MessageToUiMapperTest {

    private val timeProvider = FakeTimeProvider()
    private val mapper = MessageToUiMapper(
        todayTranslation = "Today",
        yesterdayTranslation = "Yesterday",
        timeProvider = timeProvider
    )

    @Before
    fun setup() {
        timeProvider.today = LocalDateTime.of(
            /* year = */ 2024,
            /* month = */ 3,
            /* dayOfMonth = */ 6,
            /* hour = */ 10,
            /* minute = */ 30
        )
    }

    @Test
    fun `correct view of the date representation`() {
        val today = timeProvider.today // 6.03.2024 10:30 среда

        val testedDate = today.plusDays(10).toMillis()
        val input = Message(5, "_", "_", testedDate, 10, true)

        val expected = MessageUi(5, "_", "_", "16.03.2024 10:30", 10, true)
        val actual = input.map(mapper)

        assertEquals(expected, actual)
    }

    @Test
    fun `correct view of Today date representation`() {
        val today = timeProvider.today // 6.03.2024 10:30 среда

        val todayDate = today.plusHours(5).toMillis()
        val input = Message(5, "_", "_", todayDate, 10, true)

        val expected = MessageUi(5, "_", "_", "Today 15:30", 10, true)
        val actual = input.map(mapper)

        assertEquals(expected, actual)
    }

    @Test
    fun `correct view of Yesterday date representation`() {
        val today = timeProvider.today // 6.03.2024 10:30 среда

        val yesterdayDate = today.minusDays(1).toMillis()
        val input = Message(5, "_", "_", yesterdayDate, 10, true)

        val expected = MessageUi(5, "_", "_", "Yesterday 10:30", 10, true)
        val actual = input.map(mapper)

        assertEquals(expected, actual)
    }

    @Test
    fun `correct view of 3 day before date representation`() {
        val today = timeProvider.today // 6.03.2024 10:30 среда

        val threeDayBeforeToday = today.minusDays(3).toMillis()
        val input = Message(5, "_", "_", threeDayBeforeToday, 10, true)

        val expected = MessageUi(5, "_", "_", "воскресенье 10:30", 10, true)
        val actual = input.map(mapper)

        assertEquals(expected, actual)
    }

    @Test
    fun `correct view of 6 day before date representation`() {
        val today = timeProvider.today // 6.03.2024 10:30 среда

        val sixDayBeforeToday = today.minusDays(6).toMillis()
        val input = Message(5, "_", "_", sixDayBeforeToday, 10, true)

        val expected = MessageUi(5, "_", "_", "четверг 10:30", 10, true)
        val actual = input.map(mapper)

        assertEquals(expected, actual)
    }

    @Test
    fun `correct view of 7 day before date representation`() {
        val today = timeProvider.today // 6.03.2024 10:30 среда

        val sevenDayBeforeToday = today.minusDays(7).toMillis()
        val input = Message(5, "_", "_", sevenDayBeforeToday, 10, true)

        val expected = MessageUi(5, "_", "_", "28.02.2024 10:30", 10, true)
        val actual = input.map(mapper)

        assertEquals(expected, actual)
    }

}


private class FakeTimeProvider(var today: LocalDateTime = LocalDateTime.now()) :
    CurrentTimeProvider {
    override fun now(): Long = today.toMillis()
}

private fun LocalDateTime.toMillis(): Long =
    atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()