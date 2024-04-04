package host.capitalquiz.bluetoothchat.presentation.devicesscreen

import host.capitalquiz.bluetoothchat.domain.devices.Device

data class DeviceUi(val name: String, val macAddress: String)

fun DeviceUi.toDomain() = Device(this.name, this.macAddress)

