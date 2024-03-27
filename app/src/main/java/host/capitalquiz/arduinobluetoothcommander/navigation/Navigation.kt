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
                navArgument(Screens.Chat.argumentN(0)) {},
                navArgument(Screens.Chat.argumentN(1)) {})
        ) { backStackEntry ->
            val chatName =
                backStackEntry.arguments?.getString(Screens.Chat.argumentN(0)) ?: return@composable
            val macAddress =
                backStackEntry.arguments?.getString(Screens.Chat.argumentN(1)) ?: return@composable
            val viewModel =
                hiltViewModel<BluetoothChatViewModel, BluetoothChatViewModel.Factory> { factory ->
                    factory.create(chatName, macAddress)
                }
            BluetoothChatScreen(
                viewModel = viewModel,
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
            WiFiRadioSetScreen(
                viewModel = hiltViewModel(),
                shouldDisconnect,
                openChat = { navController.navigate(Screens.ChatDevices.route) },
                onConnect = { navController.navigate(Screens.AudioConversation.route) }
            )
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

