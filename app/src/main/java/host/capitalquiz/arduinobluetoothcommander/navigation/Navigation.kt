package host.capitalquiz.arduinobluetoothcommander.navigation

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
import host.capitalquiz.bluetoothchat.presentation.chatscreen.BluetoothChatScreen
import host.capitalquiz.bluetoothchat.presentation.chatscreen.BluetoothChatViewModel
import host.capitalquiz.bluetoothchat.presentation.devicesscreen.DevicesScreen
import host.capitalquiz.wifiradioset.presentation.conversation.ConversationScreen
import host.capitalquiz.wifiradioset.presentation.conversation.ConversationViewModel
import host.capitalquiz.wifiradioset.presentation.conversation.ConversationViewModel.Factory.Companion.create
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
                viewModel = hiltViewModel(),
                onNavigateToChat = { deviceName, mac ->
                    navController.navigate(Screens.Chat.route(deviceName, mac))
                }
            )
        }
        composable(
            route = Screens.Chat.route,
            arguments = listOf(
                navArgument(Screens.Chat.CHAT_NAME) {},
                navArgument(Screens.Chat.MAC) {})
        ) { backStackEntry ->
            val chatName =
                backStackEntry.getString(Screens.Chat.CHAT_NAME) ?: return@composable
            val macAddress =
                backStackEntry.getString(Screens.Chat.MAC) ?: return@composable
            val viewModel =
                hiltViewModel<BluetoothChatViewModel, BluetoothChatViewModel.Factory> { factory ->
                    factory.create(chatName, macAddress)
                }
            BluetoothChatScreen(
                viewModel = viewModel,
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
            WiFiRadioSetScreen(
                viewModel = hiltViewModel(),
                shouldDisconnect,
                openChat = { navController.navigate(Screens.ChatDevices.route) },
                onConnect = { deviceName, mac, network ->
                    navController.navigate(
                        Screens.AudioConversation.route(deviceName, mac, network)
                    )
                }
            )
        }
        composable(
            route = Screens.AudioConversation.route,
            listOf(
                navArgument(Screens.AudioConversation.DEVICE_NAME) {},
                navArgument(Screens.AudioConversation.DEVICE_MAC) {},
                navArgument(Screens.AudioConversation.NETWORK) {})
        ) { backStackEntry ->
            val deviceName =
                backStackEntry.getString(Screens.AudioConversation.DEVICE_NAME) ?: return@composable
            val macAddress =
                backStackEntry.getString(Screens.AudioConversation.DEVICE_MAC) ?: return@composable
            val networkName =
                backStackEntry.getString(Screens.AudioConversation.NETWORK) ?: return@composable

            val viewModel =
                hiltViewModel<ConversationViewModel, ConversationViewModel.Factory> { factory ->
                    factory.create(deviceName, macAddress, networkName)
                }
            ConversationScreen(
                viewModel = viewModel,
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

fun NavBackStackEntry.getString(key: String): String? = arguments?.getString(key)

