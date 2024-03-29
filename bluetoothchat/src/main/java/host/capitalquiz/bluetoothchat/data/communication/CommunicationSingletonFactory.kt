package host.capitalquiz.bluetoothchat.data.communication

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import host.capitalquiz.bluetoothchat.domain.Communication
import host.capitalquiz.bluetoothchat.domain.SingletonFactory
import javax.inject.Inject

class CommunicationSingletonFactory @Inject constructor(
    @ApplicationContext private val context: Context,
) : SingletonFactory<Communication> {
    @Volatile
    private var communication: Communication? = null

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface CommunicationFactoryEntryPoint {
        fun communication(): Communication
    }

    private fun Communication(appContext: Context): Communication {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            CommunicationFactoryEntryPoint::class.java
        )
        return hiltEntryPoint.communication()
    }

    override fun create(): Communication {
        return synchronized(this) {
            communication ?: Communication(context)
                .also {
                    communication = it
                }
        }
    }

    override fun recycle() {
        communication = null
    }

    override fun provide(): Communication = communication ?: create()
}