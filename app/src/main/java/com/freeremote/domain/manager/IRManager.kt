package com.freeremote.domain.manager

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for infrared (IR) transmission functionality.
 *
 * This singleton class provides abstraction for IR blaster operations,
 * including checking hardware availability and transmitting IR codes
 * to control various devices like TVs, air conditioners, etc.
 *
 * @property context Application context used for accessing system services
 */
@Singleton
class IRManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val irManager: ConsumerIrManager? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
    } else {
        null
    }

    /**
     * Checks if the device has an IR emitter.
     *
     * @return True if the device has IR blaster hardware, false otherwise
     */
    fun hasIrEmitter(): Boolean {
        return irManager?.hasIrEmitter() ?: false
    }

    /**
     * Transmits an infrared pattern with the specified frequency.
     *
     * @param frequency The carrier frequency in Hz
     * @param pattern The IR pattern as alternating on/off durations in microseconds
     * @return True if transmission was successful, false otherwise
     */
    fun transmit(frequency: Int, pattern: IntArray): Boolean {
        return try {
            irManager?.transmit(frequency, pattern)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Retrieves the supported carrier frequency ranges for the IR transmitter.
     *
     * @return Array of supported frequency ranges, or null if not available
     */
    fun getCarrierFrequencies(): Array<ConsumerIrManager.CarrierFrequencyRange>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            irManager?.carrierFrequencies
        } else {
            null
        }
    }

    /**
     * Collection of common IR codes for different device brands and models.
     *
     * This object contains pre-defined IR codes for various manufacturers,
     * organized by brand and button function.
     */
    object IRCodes {
        /**
         * Samsung TV IR codes based on Samsung NEC protocol.
         * Uses Address: 0x07 for TV control commands.
         */
        val SAMSUNG_TV = mapOf(
            "POWER" to IRCode(38000, intArrayOf(
                // Samsung NEC protocol - Address: 0x07, Command: 0x02
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "VOL_UP" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "VOL_DOWN" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "CH_UP" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 562, 562, 562, 562, 1687, 562, 562, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "CH_DOWN" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "MUTE" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "OK" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 562, 562, 1687, 562, 1687, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "UP" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 1687, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "DOWN" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 1687, 562, 562, 562, 1687, 562, 562, 562, 562, 562, 1687, 562, 562, 562, 562,
                562, 562, 562, 1687, 562, 562, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 1687, 562, 562, 562, 1687, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "LEFT" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 562, 562, 1687, 562, 562, 562, 1687, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "RIGHT" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 1687, 562, 562, 562, 562, 562, 1687, 562, 562, 562, 1687, 562, 562, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 1687, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "MENU" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 1687, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "HOME" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 1687, 562, 1687, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 562, 562, 562,
                562, 562, 562, 562, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 1687, 562, 1687, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "BACK" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            // 숫자 버튼들
            "NUM_0" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 1687, 562, 562, 562, 562, 562, 562, 562, 1687, 562, 562, 562, 562, 562, 562,
                562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 1687, 562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            )),
            "NUM_1" to IRCode(38000, intArrayOf(
                9000, 4500,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562,
                562, 1687, 562, 1687, 562, 1687, 562, 562, 562, 562, 562, 562, 562, 562, 562, 1687,
                562, 562, 562, 562, 562, 562, 562, 1687, 562, 1687, 562, 1687, 562, 1687, 562, 562,
                562, 39375
            ))
        )

        /**
         * LG TV IR codes based on LG protocol.
         * Standard LG IR protocol implementation for TV control.
         */
        val LG_TV = mapOf(
            "POWER" to IRCode(38000, intArrayOf(
                9000, 4500,
                560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560,
                560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690,
                560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560,
                560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690,
                560, 39950
            )),
            "VOL_UP" to IRCode(38000, intArrayOf(
                9000, 4500,
                560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560,
                560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690,
                560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560,
                560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690,
                560, 39950
            )),
            "VOL_DOWN" to IRCode(38000, intArrayOf(
                9000, 4500,
                560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560,
                560, 1690, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690,
                560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560,
                560, 560, 560, 560, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690,
                560, 39950
            ))
        )
    }

    /**
     * Data class representing an IR code with frequency and pattern.
     *
     * @property frequency The carrier frequency in Hz (typically 38000 for most consumer devices)
     * @property pattern The IR signal pattern as alternating on/off durations in microseconds
     */
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