package com.sergisabate.fittrack.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.Job

@SuppressLint("MissingPermission")
fun scanBleDevices(
    context: Context,
    scanResults: SnapshotStateList<ScanResult>,
    scanning: Boolean,
    scanJob: Job? = null
): Boolean {
    val bluetoothAdapter: BluetoothAdapter? = getSystemService(context, BluetoothManager::class.java)?.adapter
    val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
        .build()

    val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val existingDeviceIndex = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (existingDeviceIndex != -1) {
                scanResults[existingDeviceIndex] = result
            } else {
                val deviceName = result.device.name ?: return
                if (deviceName == "MI SCALE2") {
                    Log.d("onScanResult", "Found BLE device! Name: $deviceName, address: ${result.device.address}")
                    scanResults.add(result)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("onScanFailed", "BLE Scan failed with error code: $errorCode")
        }
    }

    return if (scanning) {
        Log.d("scanBleDevices", "Stopping Scan...")
        bluetoothLeScanner?.stopScan(scanCallback)
        scanJob?.cancel()
        false
    } else {
        scanResults.clear()
        Log.d("scanBleDevices", "Starting Scan...")
        bluetoothLeScanner?.startScan(null, scanSettings, scanCallback)
        true
    }
}

