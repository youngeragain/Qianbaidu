package xcj.app.qianbaidu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigationPage() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            ConversationPage(
                onSettingClick = {
                    navController.navigate("settings")
                },

            )
        }
        composable("settings") {
            SettingsPage(
                onBack = navController::navigateUp
            )
        }
    }
}