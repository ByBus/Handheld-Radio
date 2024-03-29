package host.capitalquiz.bluetoothchat.domain

interface SingletonFactory<T> : InstanceProvider<T> {
    fun create(): T
    fun recycle()
}

interface InstanceProvider<T> {
    fun provide(): T
}
