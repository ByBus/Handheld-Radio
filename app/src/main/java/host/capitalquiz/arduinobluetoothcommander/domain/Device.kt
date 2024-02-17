package host.capitalquiz.arduinobluetoothcommander.domain


data class Device(val deviceName: String?, val mac: String) {
    fun <R> map(mapper: DeviceMapper<R>): R {
        return mapper(deviceName, mac)
    }
}

fun interface DeviceMapper<R> {
    operator fun invoke(deviceName: String?, mac: String): R
}