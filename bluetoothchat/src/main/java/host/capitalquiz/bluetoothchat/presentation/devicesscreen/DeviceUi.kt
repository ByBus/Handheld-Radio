package host.capitalquiz.bluetoothchat.presentation.devicesscreen

import host.capitalquiz.bluetoothchat.domain.Device

data class DeviceUi(val name: String, val macAddress: String)

fun DeviceUi.toDomain() = Device(this.name, this.macAddress)

