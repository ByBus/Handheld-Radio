package host.capitalquiz.bluetoothchat.domain.devices


data class Device(val deviceName: String?, val mac: String) {
    fun <R> map(mapper: DeviceMapper<R>): R {
        return mapper(deviceName, mac)
    }
}

fun interface DeviceMapper<R> {
    operator fun invoke(deviceName: String?, mac: String): R
}

fun <R> List<Device>.mapItems(mapper: DeviceMapper<R>): List<R> = this.map { it.map(mapper) }