package host.capitalquiz.wifiradioset.presentation.contracts

import android.Manifest
import host.capitalquiz.common.presentation.contracts.MultiplePermissionsContract

class RequestMicPermission : MultiplePermissionsContract() {
    override val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
}