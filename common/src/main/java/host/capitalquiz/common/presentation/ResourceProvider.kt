package host.capitalquiz.common.presentation

import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ResourceProvider<T> {
    fun provide(@IdRes id: Int): T

    class StringProvider @Inject constructor(
        @ApplicationContext private val context: Context,
    ) : ResourceProvider<String> {
        override fun provide(@StringRes id: Int): String = context.getString(id)
    }
}