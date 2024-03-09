package host.capitalquiz.arduinobluetoothcommander.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.BluetoothChatScreen
import host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen.DevicesScreen

@Composable
fun BluetoothApp(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screens.Devices.route,
        modifier = Modifier
            .fillMaxSize()
    ) {
        composable(route = Screens.Devices.route) {
            DevicesScreen(
                onNavigateToChat = { deviceName ->
                    navController.navigate(Screens.Chat.destination(deviceName))
                }
            )
        }
        composable(
            route = Screens.Chat.route,
            arguments = listOf(navArgument(Screens.Chat.arg1Name) {})
        ) { backStackEntry ->
            val chatName =
                backStackEntry.arguments?.getString(Screens.Chat.arg1Name) ?: return@composable
            BluetoothChatScreen(chatName = chatName, onDisconnect = navController::popBackStack)
        }
    }
}