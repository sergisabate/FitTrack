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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
fun PairScaleScreen(navController: NavHostController) {
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

    bluetoothDevice?.let { device ->
        BluetoothConnectionHandler(
            context = context,
            bluetoothDevice = device,
            onConnectionComplete = { name ->
                preferencesViewModel.setBluetoothDevice(
                    bluetoothDevice = name,
                    onSuccess = {
                        Toast.makeText(context, context.getString(R.string.scale_paired), Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onError = { e ->
                        Toast.makeText(context, context.getString(R.string.error_pairing_scale), Toast.LENGTH_SHORT).show()
                        Log.e("BluetoothScreen", context.getString(R.string.error_pairing_scale), e)
                    }
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
        modifier = Modifier.padding(horizontal = 16.dp),
        contentColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(scanResults) { result ->
                        DeviceCard(
                            name = result.device.name ?: stringResource(R.string.unknown),
                            onClick = { onDeviceSelected(result.device) }
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.wrapContentHeight().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isScanning,
                    onClick = onScanButtonClick
                ) {
                    Text(text = if (isScanning) stringResource(R.string.searching_scales) else stringResource(R.string.search_scales))
                }
                Spacer(modifier = Modifier.height(8.dp))
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
                Log.d("PairScale", "Bluetooth is enabled and permissions granted")
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
            onBluetoothEnabled()
        } else {
            onBluetoothDenied()
        }
    }

    LaunchedEffect(Unit) {
        val bluetoothAdapter: BluetoothAdapter? = getSystemService(context, BluetoothManager::class.java)?.adapter
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            onBluetoothEnabled()
        }
    }
}
