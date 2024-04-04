package host.capitalquiz.wifiradioset.data.devices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import host.capitalquiz.common.data.Closable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface StateDataSource<T> : Closable {
    val state: StateFlow<T>

    class LocationStateDataSource @Inject constructor(
        @ApplicationContext private val context: Context,
        private val locationManager: LocationManager,
    ) : BroadcastReceiver(), StateDataSource<LocationState> {
        private var isRegistered = false
        private val _state = MutableStateFlow(locationManager.locationState())
        override val state = _state.asStateFlow()

        override fun init() {
            if (isRegistered.not()) {
                val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
                context.registerReceiver(this, intentFilter)
                isRegistered = true
            }
        }

        override fun close() {
            if (isRegistered) {
                context.unregisterReceiver(this)
                isRegistered = false
            }
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            _state.update { locationManager.locationState() }
        }

        private fun LocationManager.locationState(): LocationState {
            val enabled = LocationManagerCompat.isLocationEnabled(this)
            return if (enabled) LocationState.Enabled else LocationState.Disabled
        }
    }
}


sealed interface LocationState {
    fun isEnabled(): Boolean

    object Enabled : LocationState {
        override fun isEnabled(): Boolean = true
    }

    object Disabled : LocationState {
        override fun isEnabled(): Boolean = false
    }
}

