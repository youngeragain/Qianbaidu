package xcj.app.qianbaidu

import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbDevice
import android.util.Log
import android.hardware.usb.UsbManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import xcj.app.qianbaidu.ui.theme.AppTheme

@Composable
fun UsbDevicePage(
    modifier: Modifier = Modifier,
    usbAction: Pair<String, Any?>?,
    onChooseFolderClick: () -> Unit,
    onChooseContentClick: () -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()
    val context = LocalContext.current

    var usbDevices by remember {
        mutableStateOf<Map<String, UsbDevice>>(emptyMap())
    }
    var usbAccessorys by remember {
        mutableStateOf<Array<UsbAccessory>>(arrayOf())
    }
    LaunchedEffect(usbAction) {
        val usbManager = ContextCompat.getSystemService<UsbManager>(context, UsbManager::class.java)
        usbManager?.deviceList?.let {
            usbDevices = it
        }
        usbManager?.accessoryList?.let {
            usbAccessorys = it
        }

    }
    Box(modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "USB Devices")
            usbDevices.forEach { (name, usbDevice) ->
                Text(name + "[${usbDevice.productName}]")
            }
            Text(text = "USB Accessorys")
            usbAccessorys.forEach { usbAccessory ->
                Text("${usbAccessory.manufacturer}[${usbAccessory.uri}]")
            }

            FilledTonalButton(onClick = onChooseFolderClick) {
                Text(
                    text = "Choose Folder"
                )
            }

            FilledTonalButton(onClick = onChooseContentClick) {
                Text(
                    text = "Choose Content"
                )
            }
        }
        if(lifecycleState == Lifecycle.State.CREATED){
            Card(modifier = Modifier
                .align(Alignment.Center)
                .size(250.dp)) {
                Text(text = "Created", fontSize = 52.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun UsbDevicePagePreview() {
    AppTheme {
        UsbDevicePage(usbAction = null, onChooseFolderClick = {}, onChooseContentClick = {})
    }
}
