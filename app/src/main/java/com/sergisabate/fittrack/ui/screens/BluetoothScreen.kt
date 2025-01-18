package com.sergisabate.fittrack.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.bluetooth.DeviceConnectionState
import com.sergisabate.fittrack.data.bluetooth.scanBleDevices
import com.sergisabate.fittrack.data.helpers.getPreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration

@SuppressLint("MissingPermission")
@Composable
fun BluetoothScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scanResults = remember { mutableStateListOf<ScanResult>() }
    var isScanning by remember { mutableStateOf(false) }
    var bluetoothDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var scanJob by remember { mutableStateOf<Job?>(null) }

    val preferencesViewModel: PreferencesViewModel = getPreferencesViewModel()
    val currentLinkedDevice by preferencesViewModel.getBluetoothDevice().collectAsState()

    ClaimPermissions(navController)

    LaunchedEffect(isScanning) {
        if (isScanning) {
            scanJob = CoroutineScope(Dispatchers.Main).launch {
                delay(Duration.ofMinutes(1))
                isScanning = scanBleDevices(context, scanResults, currentLinkedDevice, isScanning, scanJob)
            }
        } else {
            scanJob?.cancel()
        }
    }

    BluetoothScaffold(
        scanResults = scanResults,
        isScanning = isScanning,
        onDeviceSelected = { device -> bluetoothDevice = device },
        onScanButtonClick = {
            isScanning = scanBleDevices(context, scanResults, currentLinkedDevice, isScanning)
        }
    )

    val errorMessage = stringResource(R.string.unable_to_pair)
    bluetoothDevice?.let { device ->
        BluetoothConnectionHandler(
            context = context,
            bluetoothDevice = device,
            onConnectionComplete = { name ->
                preferencesViewModel.setBluetoothDevice(name,
                    onSuccess = { navController.popBackStack() },
                    onError = { Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show() }
                )
            }
        )
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothScaffold(
    scanResults: List<ScanResult>,
    isScanning: Boolean,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onScanButtonClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.wrapContentHeight()
                ) {
                    items(scanResults) { result ->
                        DeviceCard(
                            name = result.device.name ?: stringResource(R.string.unknown),
                            onClick = { onDeviceSelected(result.device) }
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.scan_info),
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = !isScanning,
                    onClick = onScanButtonClick
                ) {
                    Text(text = if (isScanning) stringResource(R.string.searching_devices) else stringResource(R.string.search_devices))
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothConnectionHandler(
    context: Context,
    bluetoothDevice: BluetoothDevice,
    onConnectionComplete: (String) -> Unit
) {
    var state by remember { mutableStateOf(DeviceConnectionState.None) }

    val callback = remember {
        object : BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                state = state.copy(gatt = gatt, connectionState = newState)

                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.device.name?.let(onConnectionComplete)
                } else {
                    Log.e("BluetoothConnectionHandler", "Error: $status")
                }
            }
        }
    }

    DisposableEffect(bluetoothDevice) {
        state = state.copy(gatt = bluetoothDevice.connectGatt(context, false, callback))
        onDispose {
            state.gatt?.disconnect()
        }
    }
}

@Composable
fun DeviceCard(
    name: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ClaimPermissions(navController: NavHostController) {
    val context = LocalContext.current
    var allPermissionsGranted: Boolean by remember { mutableStateOf(false) }
    var getBack: Boolean by remember { mutableStateOf(false) }

    val bluetoothConnectPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Manifest.permission.BLUETOOTH_CONNECT
    } else {
        Manifest.permission.BLUETOOTH
    }
    val bluetoothScanPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Manifest.permission.BLUETOOTH_SCAN
    } else {
        Manifest.permission.BLUETOOTH
    }
    val accessFineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Verifica si alguno de los permisos fue denegado
            getBack = permissions.values.any { !it }

            if (getBack) {
                navController.popBackStack()
            } else {
                permissions.entries.forEach { entry ->
                    Log.d("Permissions", "${entry.key} is granted")
                }
                allPermissionsGranted = true
            }
        }

    // Check and request permissions if necessary
    LaunchedEffect(Unit) {
        val permissionsNeeded = listOf(
            bluetoothConnectPermission,
            bluetoothScanPermission,
            accessFineLocationPermission
        ).filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
        } else {
            allPermissionsGranted = true
        }
    }

    if (allPermissionsGranted) {
        RequestEnableBluetooth(
            onBluetoothEnabled = {
                Log.d("Bluetooth", "Bluetooth is enabled and permissions granted")
            },
            onBluetoothDenied = {
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun RequestEnableBluetooth(onBluetoothEnabled: () -> Unit, onBluetoothDenied: () -> Unit) {
    val context = LocalContext.current

    val enableBluetoothLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // El usuario habilitó el Bluetooth
            onBluetoothEnabled()
        } else {
            // El usuario no habilitó el Bluetooth
            onBluetoothDenied()
        }
    }

    LaunchedEffect(Unit) {
        val bluetoothAdapter: BluetoothAdapter? = getSystemService(context, BluetoothManager::class.java)?.adapter
        if (bluetoothAdapter?.isEnabled == false) {
            // Solicitar que el usuario habilite Bluetooth
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            onBluetoothEnabled() // Bluetooth ya estaba habilitado
        }
    }
}
