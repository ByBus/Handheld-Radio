package host.capitalquiz.arduinobluetoothcommander.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import host.capitalquiz.arduinobluetoothcommander.domain.BluetoothMessage
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import host.capitalquiz.arduinobluetoothcommander.domain.Message

@SuppressLint("MissingPermission")
fun BluetoothDevice.toDevice() = Device(this.name, this.address)


fun Message.toBluetoothMessage() = BluetoothMessage(text, name, fromMe, date)