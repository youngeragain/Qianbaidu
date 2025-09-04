package xcj.app.qianbaidu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.core.content.IntentCompat

class USBBroadcastReceiver(val callback: (Pair<String, Any?>) -> Unit) : BroadcastReceiver() {
    companion object {
        private const val TAG = "USBBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        when (action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                val usbDevice = IntentCompat.getParcelableExtra<UsbDevice>(
                    intent,
                    UsbManager.EXTRA_DEVICE,
                    UsbDevice::class.java
                )
                callback(action to usbDevice)
                Log.d(TAG, "context:${context}, ACTION_USB_DEVICE_ATTACHED:${usbDevice}")
            }

            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                val usbDevice = IntentCompat.getParcelableExtra<UsbDevice>(
                    intent,
                    UsbManager.EXTRA_DEVICE,
                    UsbDevice::class.java
                )
                callback(action to usbDevice)
                Log.d(TAG, "context:${context}, ACTION_USB_DEVICE_DETACHED:${usbDevice}")
            }

            UsbManager.ACTION_USB_ACCESSORY_ATTACHED -> {
                val usbAccessory = IntentCompat.getParcelableExtra<UsbAccessory>(
                    intent,
                    UsbManager.EXTRA_ACCESSORY,
                    UsbAccessory::class.java
                )
                callback(action to usbAccessory)
                Log.d(TAG, "context:${context}, ACTION_USB_ACCESSORY_ATTACHED:${usbAccessory}")
            }

            UsbManager.ACTION_USB_ACCESSORY_DETACHED -> {
                val usbAccessory = IntentCompat.getParcelableExtra<UsbAccessory>(
                    intent,
                    UsbManager.EXTRA_ACCESSORY,
                    UsbAccessory::class.java
                )
                callback(action to usbAccessory)
                Log.d(TAG, "context:${context}, ACTION_USB_ACCESSORY_DETACHED:${usbAccessory}")
            }
        }
    }
}