package host.capitalquiz.arduinobluetoothcommander.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import host.capitalquiz.arduinobluetoothcommander.domain.Device

@SuppressLint("MissingPermission")
fun BluetoothDevice.toDevice() = Device(this.name, this.address)