package host.capitalquiz.common.core

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class WeakReferenceDelegate<T> : ReadWriteProperty<Any, T?> {
    private var weakReference = WeakReference<T>(null)

    override fun getValue(thisRef: Any, property: KProperty<*>): T? = weakReference.get()

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        weakReference = WeakReference(value)
    }
}