package com.sergisabate.fittrack.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.data.bluetooth.CHARACTERISTIC_UUID
import com.sergisabate.fittrack.data.bluetooth.DeviceConnectionState
import com.sergisabate.fittrack.data.bluetooth.SERVICE_UUID
import com.sergisabate.fittrack.data.bluetooth.parseBytes
import com.sergisabate.fittrack.data.bluetooth.scanBleDevices
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.repositories.WeightsRepository
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModelFactory
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModelFactory

@SuppressLint("MissingPermission")
@Composable
fun WeighScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scanResults = remember { mutableStateListOf<ScanResult>() }
    val isScanning by remember { mutableStateOf(false) }
    var bluetoothDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    val preferencesViewModel: PreferencesViewModel = getPreferencesViewModel()
    val currentLinkedDevice by preferencesViewModel.getBluetoothDevice().collectAsState()

    PermissionsHandler(navController)

    LaunchedEffect(isScanning) {
        scanBleDevices(context, scanResults, "", isScanning)
    }

    val bluetoothAdapter: BluetoothAdapter? = getSystemService(context, BluetoothManager::class.java)?.adapter
    val bondedDevices = remember { bluetoothAdapter?.bondedDevices?.toList() }

    bluetoothDevice = bondedDevices?.find { it.name == currentLinkedDevice }
        ?: scanResults.firstOrNull { it.device.name == currentLinkedDevice }?.device

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            bluetoothDevice != null -> {
                ConnectDeviceScreen(device = bluetoothDevice!!, navController = navController)
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        strokeWidth = 8.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionsHandler(navController: NavHostController) {
    val context = LocalContext.current
    var permissionsGranted by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allPermissionsGranted = permissions.values.all { it }
        if (allPermissionsGranted) {
            permissionsGranted = true
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        val permissionsNeeded = listOf(
            getBluetoothConnectPermission(),
            getBluetoothScanPermission(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ).filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
        } else {
            permissionsGranted = true
        }
    }

    if (permissionsGranted) {
        BluetoothEnabler()
    }
}

@Composable
private fun BluetoothEnabler() {
    val context = LocalContext.current

    val enableBluetoothLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("Bluetooth", "Bluetooth enabled")
        } else {
            Log.d("Bluetooth", "Bluetooth not enabled")
        }
    }

    LaunchedEffect(Unit) {
        val bluetoothAdapter: BluetoothAdapter? = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        }
    }
}

@SuppressLint("InlinedApi")
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun ConnectDeviceScreen(device: BluetoothDevice, navController: NavHostController) {
    val context = LocalContext.current

    // Estado de la conexión del dispositivo Bluetooth
    var connectionState by remember(device) {
        mutableStateOf<DeviceConnectionState?>(null)
    }

    // Obtener el servicio específico
    val service = remember(connectionState?.services) {
        connectionState?.services?.find { it.uuid == SERVICE_UUID }
    }

    // Obtener la característica específica del servicio
    val characteristic = remember(service) {
        service?.getCharacteristic(CHARACTERISTIC_UUID)
    }

    val weightsViewModel: WeightsViewModel = getWeightsViewModel()

    // Estado de la habilitación de notificaciones y la recepción de datos
    var notificationEnabled by remember { mutableStateOf(false) }
    var blockDataReception by remember { mutableStateOf(false) }

    // Efecto para gestionar el estado de la conexión y actualizar la UI
    BLEConnectEffect(device = device, blockDataReception = blockDataReception) {
        connectionState = it
    }

    // Descubrir servicios cuando se monta el Composable
    LaunchedEffect(connectionState?.gatt) {
        connectionState?.gatt?.discoverServices()
    }

    // Habilitar notificaciones para la característica específica si es necesario
    LaunchedEffect(characteristic, notificationEnabled) {
        if (connectionState?.gatt != null && !notificationEnabled) {
            if (connectionState?.gatt?.services.isNullOrEmpty()) {
                connectionState?.gatt?.discoverServices()
            } else {
                notificationEnabled = connectionState?.gatt
                    ?.setCharacteristicNotification(characteristic, true) == true
            }
        }
    }

    // Reconectar si la conexión se pierde
    LaunchedEffect(connectionState?.connectionState) {
        if (connectionState?.connectionState == BluetoothProfile.STATE_DISCONNECTED) {
            connectionState?.gatt?.connect()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val isStabilized = connectionState?.isStabilized == true
        val weight = connectionState?.weight ?: 0f

        if (isStabilized) {
            blockDataReception = true
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Este Spacer empuja el contenido hacia el centro
            Spacer(modifier = Modifier.weight(1f))

            // El peso se centra en la pantalla
            val weightString = "%.2f".format(weight)
            Text(
                text = "$weightString kg",
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isStabilized) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )

            // Este Spacer empuja el contenido hacia abajo
            Spacer(modifier = Modifier.weight(1f))

            // El contenido inferior
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿Quieres guardar este peso?",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isStabilized) MaterialTheme.colorScheme.primary else Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Button(
                        onClick = { if (isStabilized) {
                            weightsViewModel.addOrUpdateWeightRecord(
                                weight,
                                onSuccess = { navController.popBackStack()},
                                onError = { Toast.makeText(context, "No se ha podido guardar el peso", Toast.LENGTH_SHORT).show()}
                            )
                        }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isStabilized
                    ) {
                        Text(text = "Sí", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { if (isStabilized) {
                            blockDataReception = false
                            connectionState = connectionState?.copy(isStabilized = false)
                            connectionState = connectionState?.copy(weight = 0f)
                        } },
                        modifier = Modifier.weight(1f),
                        enabled = isStabilized
                    ) {
                        Text(text = "No", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
private fun BLEConnectEffect(
    device: BluetoothDevice,
    blockDataReception: Boolean,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onStateChange: (DeviceConnectionState) -> Unit
) {
    val context = LocalContext.current
    val currentOnStateChange by rememberUpdatedState(onStateChange)
    val currentBlockDataReception by rememberUpdatedState(blockDataReception)

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
                    Log.e("BLEConnectEffect", "An error happened: $status")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                state = state.copy(services = gatt.services)
                currentOnStateChange(state)
            }

            @SuppressLint("MissingPermission")
            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                super.onCharacteristicChanged(gatt, characteristic, value)

                // Bloquear la recepción de nuevos datos si isStabilized es true
                Log.d("BLEConnectEffect", "blockDataReception: $currentBlockDataReception")
                if (currentBlockDataReception) return

                if (value.size != 10) return  // Salir si el tamaño de value no es el esperado

                val record = parseBytes(value)

                state = state.copy(
                    weight = record.weight,
                    isStabilized = record.isStabilized
                )

                currentOnStateChange(state)
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (state.gatt != null) {
                    state.gatt?.connect()
                } else {
                    state = state.copy(gatt = device.connectGatt(context, false, callback))
                }
            } else if (event == Lifecycle.Event.ON_STOP) {
                state.gatt?.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            state.gatt?.close()
            state = DeviceConnectionState.None
        }
    }
}

@Composable
private fun getWeightsViewModel(): WeightsViewModel {
    val context = LocalContext.current
    val weightsRepository = remember { WeightsRepository.getInstance(context) }
    return viewModel(factory = WeightsViewModelFactory(weightsRepository))
}

@Composable
private fun getPreferencesViewModel(): PreferencesViewModel {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    return viewModel(factory = PreferencesViewModelFactory(userPreferencesRepository))
}

private fun getBluetoothConnectPermission(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Manifest.permission.BLUETOOTH_CONNECT
    } else {
        Manifest.permission.BLUETOOTH
    }
}

private fun getBluetoothScanPermission(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Manifest.permission.BLUETOOTH_SCAN
    } else {
        Manifest.permission.BLUETOOTH
    }
}
