package host.capitalquiz.bluetoothchat.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import host.capitalquiz.bluetoothchat.domain.BluetoothMessage
import host.capitalquiz.bluetoothchat.domain.Device
import host.capitalquiz.bluetoothchat.domain.Message

@SuppressLint("MissingPermission")
fun BluetoothDevice.toDevice() = Device(this.name, this.address)


fun Message.toBluetoothMessage() = BluetoothMessage(text, name, fromMe, date)