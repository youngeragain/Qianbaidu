package xcj.app.qianbaidu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.documentfile.provider.DocumentFile
import xcj.app.qianbaidu.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private var documentResultLauncher: ActivityResultLauncher<Uri?>? = null
    private var contentResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private val usbActionState: MutableState<Pair<String, Any?>?> = mutableStateOf(null)

    private lateinit var usbBroadcastReceiver: USBBroadcastReceiver

    private val mainViewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        val windowInsetsControllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        // windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars())
//        window.attributes = window.attributes.apply {
//            flags = flags or WindowManager.LayoutParams.FLAG_SECURE
//        }
        setContent {
            AppTheme {
                Scaffold { paddingValues ->
                    MainNavigationPage()
                }
            }
        }
        documentResultLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            if (uri == null) {
                return@registerForActivityResult
            }
            val takeFlags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)
            val folderRootDocumentFile = DocumentFile.fromTreeUri(this, uri)
            if (folderRootDocumentFile == null) {
                return@registerForActivityResult
            }
            Log.d(
                TAG,
                "file:${folderRootDocumentFile.name}, isDictionary:${folderRootDocumentFile.isDirectory}, uri:${folderRootDocumentFile.uri}"
            )

            folderRootDocumentFile.creteFileInFolder(this, "application/text", "a_text_file.txt") {
                write("this is a text file")
                flush()
            }
            folderRootDocumentFile.listFiles().forEach { documentFile ->
                Log.d(
                    TAG,
                    "file:${documentFile.name}, isDictionary:${documentFile?.isDirectory}, documentFile.type:${documentFile.type}"
                )
            }
        }

        contentResultLauncher = registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia()
        ) { uri ->
            Log.d(TAG, "content select result:$uri")
        }

        usbBroadcastReceiver = USBBroadcastReceiver {
            usbActionState.value = it
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(usbBroadcastReceiver, intentFilter)

        // Access the ViewModel to trigger its initialization if not already done.
        Log.d(TAG, "MainViewModel instance: $mainViewModel")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbBroadcastReceiver)
    }
}
