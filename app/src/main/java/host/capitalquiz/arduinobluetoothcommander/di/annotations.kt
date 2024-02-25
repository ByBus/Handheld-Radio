package host.capitalquiz.arduinobluetoothcommander.di

import javax.inject.Qualifier

@Qualifier
annotation class PairedDevices

@Qualifier
annotation class ScannedDevices

@Qualifier
annotation class DispatcherIO

@Qualifier
annotation class DispatcherMain