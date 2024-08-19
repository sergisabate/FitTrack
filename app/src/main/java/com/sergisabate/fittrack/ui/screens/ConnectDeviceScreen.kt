package com.sergisabate.fittrack.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sergisabate.fittrack.data.bluetooth.GATTServerSampleService.Companion.CHARACTERISTIC_UUID
import com.sergisabate.fittrack.data.bluetooth.GATTServerSampleService.Companion.SERVICE_UUID
import com.sergisabate.fittrack.data.bluetooth.parseBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class DeviceConnectionState(
    val gatt: BluetoothGatt?,
    val connectionState: Int,
    val mtu: Int = 27,
    val services: List<BluetoothGattService> = emptyList(),
    val messageSent: Boolean = false,
    val messageReceived: String = "",
) {
    companion object {
        val None = DeviceConnectionState(null, -1, -1)
    }
}

@SuppressLint("InlinedApi")
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun ConnectDeviceScreen(device: BluetoothDevice, onClose: () -> Unit) {
    val scope = rememberCoroutineScope()

    // Keeps track of the last connection state with the device
    var state by remember(device) {
        mutableStateOf<DeviceConnectionState?>(null)
    }
    // Once the device services are discovered find the GATTServerSample service
    val service by remember(state?.services) {
        mutableStateOf(state?.services?.find { it.uuid == SERVICE_UUID })
    }
    // If the GATTServerSample service is found, get the characteristic
    val characteristic by remember(service) {
        mutableStateOf(service?.getCharacteristic(CHARACTERISTIC_UUID))
    }

    // This effect will handle the connection and notify when the state changes
    BLEConnectEffect(device = device) {
        // update our state to recompose the UI
        state = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Devices details",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Name: ${device.name} (${device.address})",
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Status: ${state?.connectionState?.toConnectionStateString()}",
            color = MaterialTheme.colorScheme.primary
        )
        val serviceSelected = state?.services?.firstOrNull { it.uuid == SERVICE_UUID }
        if (serviceSelected != null) {
            Text(
                text = "Characteristics: ${serviceSelected.characteristics?.joinToString { it.uuid.toString() }}",
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Message sent: ${state?.messageSent}",
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Message received: ${state?.messageReceived}",
            color = MaterialTheme.colorScheme.primary
        )
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    if (state?.connectionState == BluetoothProfile.STATE_DISCONNECTED) {
                        state?.gatt?.connect()
                    }
                }
            },
        ) {
            Text(text = "Connect")
        }
        Button(
            enabled = state?.gatt != null,
            onClick = {
                scope.launch(Dispatchers.IO) {
                    // Once we have the connection discover the peripheral services
                    state?.gatt?.discoverServices()
                }
            },
        ) {
            Text(text = "Discover")
        }
        Button(
            enabled = state?.gatt != null && characteristic != null,
            onClick = {
                scope.launch(Dispatchers.IO) {
                    if (state?.connectionState == BluetoothProfile.STATE_DISCONNECTED) {
                        state?.gatt?.connect()
                    }
                    // Once we have the connection discover the peripheral services
                    state?.gatt?.setCharacteristicNotification(characteristic, true)
                    sendData(state?.gatt!!, characteristic!!)
                }
            },
        ) {
            Text(text = "Testing")
        }
        Button(
            enabled = state?.gatt != null && characteristic != null,
            onClick = {
                scope.launch(Dispatchers.IO) {
                    sendData(state?.gatt!!, characteristic!!)
                }
            },
        ) {
            Text(text = "Write to server")
        }
        Button(
            enabled = state?.gatt != null && characteristic != null,
            onClick = {
                scope.launch(Dispatchers.IO) {
                    state?.gatt?.readCharacteristic(characteristic)
                }
            },
        ) {
            Text(text = "Read characteristic")
        }
        Button(onClick = onClose) {
            Text(text = "Close")
        }
    }
}

/**
 * Writes "hello world" to the server characteristic
 */
@SuppressLint("MissingPermission")
private fun sendData(
    gatt: BluetoothGatt,
    characteristic: BluetoothGattCharacteristic,
) {
    val data = byteArrayOf(0x02.toByte())
    //val data = "Hello world!".toByteArray()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        gatt.writeCharacteristic(
            characteristic,
            data,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT,
        )
    } else {
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        @Suppress("DEPRECATION")
        characteristic.value = data
        @Suppress("DEPRECATION")
        gatt.writeCharacteristic(characteristic)
    }
}

internal fun Int.toConnectionStateString() = when (this) {
    BluetoothProfile.STATE_CONNECTED -> "Connected"
    BluetoothProfile.STATE_CONNECTING -> "Connecting"
    BluetoothProfile.STATE_DISCONNECTED -> "Disconnected"
    BluetoothProfile.STATE_DISCONNECTING -> "Disconnecting"
    else -> "N/A"
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
private fun BLEConnectEffect(
    device: BluetoothDevice,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onStateChange: (DeviceConnectionState) -> Unit,
) {
    val context = LocalContext.current
    val currentOnStateChange by rememberUpdatedState(onStateChange)

    // Keep the current connection state
    var state by remember {
        mutableStateOf(DeviceConnectionState.None)
    }

    DisposableEffect(lifecycleOwner, device) {
        // This callback will notify us when things change in the GATT connection so we can update
        // our state
        val callback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(
                gatt: BluetoothGatt,
                status: Int,
                newState: Int,
            ) {
                super.onConnectionStateChange(gatt, status, newState)
                state = state.copy(gatt = gatt, connectionState = newState)
                currentOnStateChange(state)

                if (status != BluetoothGatt.GATT_SUCCESS) {
                    // Here you should handle the error returned in status based on the constants
                    // https://developer.android.com/reference/android/bluetooth/BluetoothGatt#summary
                    // For example for GATT_INSUFFICIENT_ENCRYPTION or
                    // GATT_INSUFFICIENT_AUTHENTICATION you should create a bond.
                    // https://developer.android.com/reference/android/bluetooth/BluetoothDevice#createBond()
                    Log.e("BLEConnectEffect", "An error happened: $status")
                }
            }

            override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
                super.onMtuChanged(gatt, mtu, status)
                state = state.copy(gatt = gatt, mtu = mtu)
                currentOnStateChange(state)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                state = state.copy(services = gatt.services)
                currentOnStateChange(state)
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int,
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                state = state.copy(messageSent = status == BluetoothGatt.GATT_SUCCESS)
                currentOnStateChange(state)
            }

            @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int,
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    doOnRead(characteristic.value)
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray,
                status: Int,
            ) {
                super.onCharacteristicRead(gatt, characteristic, value, status)
                doOnRead(value)
            }

            @SuppressLint("MissingPermission")
            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                super.onCharacteristicChanged(gatt, characteristic, value)
                Log.d("CHARATERISTICCHANGED", "onCharacteristicChanged: $characteristic")
                Log.d("CHARATERISTICCHANGED", "onCharacteristicChanged: $value")
                if (value.size == 10) {
                    parseBytes(value)
                }

            }

            private fun doOnRead(value: ByteArray) {
                val data = value
                val scaleYear = ((data[1].toInt() and 0xFF) shl 8) or (data[0].toInt() and 0xFF)
                val scaleMonth = data[2].toInt()
                val scaleDay = data[3].toInt()
                // state = state.copy(messageReceived = value.decodeToString())
                state = state.copy(messageReceived = "$scaleDay/$scaleMonth/$scaleYear")
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (state.gatt != null) {
                    // If we previously had a GATT connection let's reestablish it
                    state.gatt?.connect()
                } else {
                    // Otherwise create a new GATT connection
                    state = state.copy(gatt = device.connectGatt(context, false, callback))
                }
            } else if (event == Lifecycle.Event.ON_STOP) {
                // Unless you have a reason to keep connected while in the bg you should disconnect
                state.gatt?.disconnect()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer and close the connection
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            state.gatt?.close()
            state = DeviceConnectionState.None
        }
    }
}
