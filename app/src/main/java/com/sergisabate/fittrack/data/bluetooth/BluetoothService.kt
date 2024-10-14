package com.sergisabate.fittrack.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.UUID

val SERVICE_UUID: UUID = UUID.fromString("0000181d-0000-1000-8000-00805f9b34fb")
val CHARACTERISTIC_UUID: UUID = UUID.fromString(String.format("%08x%s", 0x2a9d, "-0000-1000-8000-00805f9b34fb"))
// Fecha
// val CHARACTERISTIC_UUID: UUID = UUID.fromString(String.format("%08x%s", 0x2a2b, "-0000-1000-8000-00805f9b34fb"))

@SuppressLint("MissingPermission")
fun scanBleDevices(
    context: Context,
    scanResults: SnapshotStateList<ScanResult>,
    currentLinkedDevice: String = "",
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
                if (deviceName != currentLinkedDevice) {
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

@SuppressLint("SimpleDateFormat")
fun parseBytes(weightBytes: ByteArray) : WeightResult {
    try {
        val ctrlByte = weightBytes[0]

        val isWeightRemoved = isBitSet(ctrlByte, 7)
        val isStabilized = isBitSet(ctrlByte, 5)
        val isLBSUnit = isBitSet(ctrlByte, 0)
        val isCattyUnit = isBitSet(ctrlByte, 4)

        if (!isWeightRemoved) {

            val year = ((weightBytes[4].toInt() and 0xFF) shl 8) or (weightBytes[3].toInt() and 0xFF)
            val month = weightBytes[5].toInt()
            val day = weightBytes[6].toInt()
            val hours = weightBytes[7].toInt()
            val min = weightBytes[8].toInt()
            //val sec = weightBytes[9].toInt()

            val weight = if (isLBSUnit || isCattyUnit) {
                (((weightBytes[2].toInt() and 0xFF) shl 8) or (weightBytes[1].toInt() and 0xFF)) / 100.0f
            } else {
                (((weightBytes[2].toInt() and 0xFF) shl 8) or (weightBytes[1].toInt() and 0xFF)) / 200.0f
            }

            val dateString = "$year/$month/$day/$hours/$min"
            val dateTime = SimpleDateFormat("yyyy/MM/dd/HH/mm").parse(dateString)
            val test = "%.2f".format(weight)
            Log.d("BLUETOOTH", "Weight: $test, Date: $dateTime, isStabilized: $isStabilized, LBS: $isLBSUnit, Catty: $isCattyUnit")
            return WeightResult(weight,isStabilized)
        }
    } catch (e: ParseException) {
        Log.e("UNEXPECTED_ERROR", "Error while decoding bluetooth date string (${e.message})")
    }

    return WeightResult()
}

fun isBitSet(value: Byte, bit: Int): Boolean {
    return (value.toInt() and (1 shl bit)) != 0
}

data class DeviceConnectionState(
    val gatt: BluetoothGatt?,
    val connectionState: Int,
    val services: List<BluetoothGattService> = emptyList(),
    val weight: Float = 0f,
    val isStabilized: Boolean = false
) {
    companion object {
        val None = DeviceConnectionState(null, -1, emptyList(), 0f)
    }
}

data class WeightResult (
    val weight: Float = 0.0f,
    val isStabilized: Boolean = false
)
