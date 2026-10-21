package com.sergisabate.fittrack.data.bluetooth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BluetoothServiceTest {

    private fun bytes(vararg values: Int) = ByteArray(values.size) { values[it].toByte() }

    @Test
    fun `stabilized weight in kg uses 0,005 kg resolution`() {
        val result = parseBytes(bytes(0x20, 0xFC, 0x3A))
        assertEquals(75.5f, result.weight, 0.001f)
        assertTrue(result.isStabilized)
    }

    @Test
    fun `weight in lbs uses 0,01 lb resolution`() {
        val result = parseBytes(bytes(0x01, 0x00, 0x41))
        assertEquals(166.4f, result.weight, 0.001f)
        assertFalse(result.isStabilized)
    }

    @Test
    fun `weight removed from the scale returns empty result`() {
        assertEquals(WeightResult(), parseBytes(bytes(0x80, 0xFC, 0x3A)))
    }

    @Test
    fun `malformed short packet returns empty result instead of crashing`() {
        assertEquals(WeightResult(), parseBytes(bytes(0x20)))
        assertEquals(WeightResult(), parseBytes(ByteArray(0)))
    }
}
