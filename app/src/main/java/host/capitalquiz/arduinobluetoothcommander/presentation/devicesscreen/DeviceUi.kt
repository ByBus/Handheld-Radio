package host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen

import host.capitalquiz.arduinobluetoothcommander.domain.Device

data class DeviceUi(val name: String, val macAddress: String)

fun DeviceUi.toDomain() = Device(this.name, this.macAddress)

