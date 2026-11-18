package com.poolcompanion.app.calculator

import kotlin.math.atan2
import kotlin.math.hypot

data class ShotResult(
    val angle: Double,
    val distance: Double
)

object ShotCalculator {

    fun calculate(
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double
    ): ShotResult? {

        val dx = x2 - x1
        val dy = y2 - y1

        if (dx == 0.0 && dy == 0.0) return null

        return ShotResult(
            angle = Math.toDegrees(atan2(dy, dx)),
            distance = hypot(dx, dy)
        )
    }
}
