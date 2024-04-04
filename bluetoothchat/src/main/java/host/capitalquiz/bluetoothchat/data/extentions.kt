package host.capitalquiz.bluetoothchat.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import host.capitalquiz.bluetoothchat.domain.chat.BluetoothMessage
import host.capitalquiz.bluetoothchat.domain.devices.Device
import host.capitalquiz.bluetoothchat.domain.chat.Message

@SuppressLint("MissingPermission")
fun BluetoothDevice.toDevice() = Device(this.name, this.address)


fun Message.toBluetoothMessage() = BluetoothMessage(text, name, fromMe, date)