package xcj.app.qianbaidu

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
                }
            )
        }
        composable("settings") {
            val context = LocalContext.current
            val activity = context as ComponentActivity
            val viewModel: MainViewModel = viewModel<MainViewModel>(viewModelStoreOwner = activity)
            SettingsPage(
                onBack = navController::navigateUp,
                onClearHistory = {
                    viewModel.clearHistory()
                },
                onClearCaches = {

                }
            )
        }
    }
}