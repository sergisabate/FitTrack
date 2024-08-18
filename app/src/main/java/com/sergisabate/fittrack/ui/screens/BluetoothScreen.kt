package com.sergisabate.fittrack.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.bluetooth.scanBleDevices
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
    var scanJob by remember { mutableStateOf<Job?>(null) }

    // Ensure permissions are claimed
    ClaimPermissions(navController)

    LaunchedEffect(isScanning) {
        if (isScanning) {
            // Start a coroutine job to stop scanning after 15 seconds
            scanJob = CoroutineScope(Dispatchers.Main).launch {
                delay(Duration.ofSeconds(15)) // Delay for 15 seconds
                isScanning = scanBleDevices(context, scanResults, isScanning, scanJob)
            }
        } else {
            scanJob?.cancel() // Cancel the job if scanning is stopped manually
        }
    }

    // Main Scaffold layout
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
                    .padding(bottom = 56.dp) // Add padding at the bottom to make space for the button
            ) {
                LazyColumn(
                    modifier = Modifier.wrapContentHeight()
                ) {
                    items(scanResults) { result ->
                        DeviceCard(
                            name = result.device.name ?: "Unknown",
                            address = result.device.address ?: "Unknown",
                            icon = painterResource(id = R.drawable.ic_launcher_foreground)
                        )
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter),
                enabled = !isScanning,
                onClick = {
                    isScanning = scanBleDevices(
                        context = context,
                        scanResults = scanResults,
                        scanning = isScanning,
                        scanJob = scanJob
                    )
                }
            ) {
                Text(text = if (isScanning) "Buscando Dispositivos..." else "Buscar Dispositivos")
            }
        }
    }
}

@Composable
private fun ClaimPermissions(navController: NavHostController) {
    val context = LocalContext.current
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
        }
    }
}

@Composable
fun DeviceCard(name: String, address: String, icon: Painter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
            )
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}
