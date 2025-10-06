package com.freeremote.domain.manager

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IRManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val irManager: ConsumerIrManager? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
    } else {
        null
    }

    fun hasIrEmitter(): Boolean {
        return irManager?.hasIrEmitter() ?: false
    }

    fun transmit(frequency: Int, pattern: IntArray): Boolean {
        return try {
            irManager?.transmit(frequency, pattern)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getCarrierFrequencies(): Array<ConsumerIrManager.CarrierFrequencyRange>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            irManager?.carrierFrequencies
        } else {
            null
        }
    }

    // Common IR codes for different devices
    object IRCodes {
        // Samsung TV codes (example)
        val SAMSUNG_TV = mapOf(
            "POWER" to IRCode(38000, intArrayOf(
                9000, 4500, 562, 562, 562, 562, 562, 1687,
                562, 1687, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 1687,
                562, 1687, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 1687,
                562, 1687, 562, 1687, 562, 1687, 562, 1687,
                562, 1687
            )),
            "VOL_UP" to IRCode(38000, intArrayOf(
                9000, 4500, 562, 562, 562, 562, 562, 1687,
                562, 1687, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 1687,
                562, 1687, 562, 1687, 562, 1687, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 562, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 1687,
                562, 1687
            )),
            "VOL_DOWN" to IRCode(38000, intArrayOf(
                9000, 4500, 562, 562, 562, 562, 562, 1687,
                562, 1687, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 1687,
                562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 1687, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 1687, 562, 1687, 562, 1687,
                562, 1687
            ))
        )

        // LG TV codes (example)
        val LG_TV = mapOf(
            "POWER" to IRCode(38000, intArrayOf(
                9000, 4500, 560, 560, 560, 560, 560, 1690,
                560, 560, 560, 560, 560, 560, 560, 560,
                560, 560, 560, 1690, 560, 1690, 560, 560,
                560, 1690, 560, 1690, 560, 1690, 560, 1690,
                560, 1690, 560, 560, 560, 560, 560, 560,
                560, 1690, 560, 560, 560, 560, 560, 560,
                560, 560, 560, 1690, 560, 1690, 560, 1690,
                560, 560, 560, 1690, 560, 1690, 560, 1690,
                560, 1690
            ))
        )
    }

    data class IRCode(
        val frequency: Int,
        val pattern: IntArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as IRCode

            if (frequency != other.frequency) return false
            if (!pattern.contentEquals(other.pattern)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = frequency
            result = 31 * result + pattern.contentHashCode()
            return result
        }
    }
}