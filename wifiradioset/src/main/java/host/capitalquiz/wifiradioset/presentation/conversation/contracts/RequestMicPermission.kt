package host.capitalquiz.wifiradioset.presentation.conversation.contracts

import android.Manifest
import host.capitalquiz.common.presentation.contracts.MultiplePermissionsContract

class RequestMicPermission : MultiplePermissionsContract("", "") {
    override val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
}