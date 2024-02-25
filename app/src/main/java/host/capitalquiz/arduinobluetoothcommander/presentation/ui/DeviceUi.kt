package host.capitalquiz.arduinobluetoothcommander.presentation.ui

import host.capitalquiz.arduinobluetoothcommander.domain.Device

data class DeviceUi(val name: String, val macAddress: String)

fun DeviceUi.toDomain() = Device(this.name, this.macAddress)

