package host.capitalquiz.wifiradioset.presentation.conversation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import host.capitalquiz.common.SingleEventEffect

@Composable
fun ConversationScreen(viewModel: ConversationViewModel, onDisconnect: () -> Unit) {

    val context = LocalContext.current
    SingleEventEffect(sideEffectFlow = viewModel.event) { event ->
        event
            .message { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            .navigate(onDisconnect)
    }

    Box {
        Button(
            onClick = {
                Log.d("ConversationViewModelM", "ConversationScreen: click to send")
                viewModel.send("Hello world " + (1..10).random())
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Send message")
        }
    }
}