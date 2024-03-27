package host.capitalquiz.arduinobluetoothcommander.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.BluetoothChatScreen
import host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen.DevicesScreen
import host.capitalquiz.wifiradioset.presentation.conversation.ConversationScreen
import host.capitalquiz.wifiradioset.presentation.devices.WiFiRadioSetScreen

@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screens.RadioSetDevices.route,
        modifier = Modifier
            .fillMaxSize()
    ) {
        composable(route = Screens.ChatDevices.route) {
            DevicesScreen(
                onNavigateToChat = { deviceName, mac ->
                    navController.navigate(Screens.Chat.route(deviceName, mac))
                }
            )
        }
        composable(
            route = Screens.Chat.route,
            arguments = listOf(
                navArgument(Screens.Chat.argumentN(0)) {},
                navArgument(Screens.Chat.argumentN(1)) {})
        ) { backStackEntry ->
            val chatName =
                backStackEntry.arguments?.getString(Screens.Chat.argumentN(0)) ?: return@composable
            val macAddress =
                backStackEntry.arguments?.getString(Screens.Chat.argumentN(1)) ?: return@composable
            BluetoothChatScreen(
                chatName = chatName,
                deviceMac = macAddress,
                onDisconnect = navController::popBackStack
            )
        }
        composable(
            route = Screens.RadioSetDevices.route,
        ) { backStackEntry ->
            val shouldDisconnect = backStackEntry.popSavedStateHandleValue(
                key = Screens.RadioSetDevices.DISCONNECT,
                default = false
            )
            WiFiRadioSetScreen(viewModel = hiltViewModel(), shouldDisconnect) {
                navController.navigate(Screens.AudioConversation.route)
            }
        }
        composable(route = Screens.AudioConversation.route) {
            ConversationScreen(
                viewModel = hiltViewModel(),
                onDisconnect = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        Screens.RadioSetDevices.DISCONNECT,
                        true
                    )
                    navController.popBackStack()
                }
            )
        }
    }
}


@Composable
fun <T> NavBackStackEntry.popSavedStateHandleValue(key: String, default: T): T =
    savedStateHandle.getStateFlow(key, default).collectAsState().value.also {
        savedStateHandle.remove<T>(key)
    }

