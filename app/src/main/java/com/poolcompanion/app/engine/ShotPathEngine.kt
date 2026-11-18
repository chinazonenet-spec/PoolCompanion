
package com.poolcompanion.app.engine

import kotlin.math.abs
import kotlin.math.hypot

enum class ShotMode {
    DIRECT,
    ONE_CUSHION,
    TWO_CUSHION
}

data class RoutePoint(
    val x: Double,
    val y: Double,
    val cushion: String
)

data class ShotPath(
    val bouncePoints: List<RoutePoint>,
    val distance: Double,
    val routeName: String
)

object ShotPathEngine {

    private fun fold(value: Double, size: Double): Double {
        val period = 2.0 * size
        val position = ((value % period) + period) % period

        return if (position <= size) {
            position
        } else {
            period - position
        }
    }

    fun calculate(
        startX: Double,
        startY: Double,
        targetX: Double,
        targetY: Double,
        mode: ShotMode,
        tableWidth: Double = 1000.0,
        tableHeight: Double = 500.0
    ): ShotPath? {

        if (tableWidth <= 0.0 || tableHeight <= 0.0) return null

        if (startX !in 0.0..tableWidth ||
            targetX !in 0.0..tableWidth ||
            startY !in 0.0..tableHeight ||
            targetY !in 0.0..tableHeight) return null

        if (startX == targetX && startY == targetY) return null

        if (mode == ShotMode.DIRECT) {
            return ShotPath(
                emptyList(),
                hypot(targetX - startX, targetY - startY),
                "DIRECT"
            )
        }

        val images = when (mode) {
            ShotMode.ONE_CUSHION -> listOf(
                -1 to 0,
                1 to 0,
                0 to -1,
                0 to 1
            )
            ShotMode.TWO_CUSHION -> listOf(
                -1 to -1,
                -1 to 1,
                1 to -1,
                1 to 1
            )
            else -> emptyList()
        }

        val candidates = mutableListOf<ShotPath>()

        for ((kx, ky) in images) {

            val imageX = when (kx) {
                -1 -> -targetX
                1 -> 2.0 * tableWidth - targetX
                else -> targetX
            }

            val imageY = when (ky) {
                -1 -> -targetY
                1 -> 2.0 * tableHeight - targetY
                else -> targetY
            }

            val dx = imageX - startX
            val dy = imageY - startY

            val points = mutableListOf<Pair<Double, RoutePoint>>()

            if (kx != 0) {
                if (dx == 0.0) continue

                val wallX = if (kx == -1) 0.0 else tableWidth
                val t = (wallX - startX) / dx
                val rawY = startY + t * dy

                if (t <= 0.0 || t >= 1.0) continue

                val y = if (mode == ShotMode.TWO_CUSHION) {
                    fold(rawY, tableHeight)
                } else {
                    rawY
                }

                if (y !in 0.0..tableHeight) continue

                points.add(
                    t to RoutePoint(
                        wallX,
                        y,
                        if (kx == -1) "LEFT" else "RIGHT"
                    )
                )
            }

            if (ky != 0) {
                if (dy == 0.0) continue

                val wallY = if (ky == -1) 0.0 else tableHeight
                val t = (wallY - startY) / dy
                val rawX = startX + t * dx

                if (t <= 0.0 || t >= 1.0) continue

                val x = if (mode == ShotMode.TWO_CUSHION) {
                    fold(rawX, tableWidth)
                } else {
                    rawX
                }

                if (x !in 0.0..tableWidth) continue

                points.add(
                    t to RoutePoint(
                        x,
                        wallY,
                        if (ky == -1) "TOP" else "BOTTOM"
                    )
                )
            }

            val required = if (
                mode == ShotMode.ONE_CUSHION
            ) 1 else 2

            if (points.size != required) continue

            points.sortBy { it.first }

            if (points.size == 2 &&
                abs(points[0].first - points[1].first) < 0.000001
            ) continue

            val route = points.map { it.second }

            candidates.add(
                ShotPath(
                    bouncePoints = route,
                    distance = hypot(dx, dy),
                    routeName = route.joinToString(" → ") {
                        it.cushion
                    }
                )
            )
        }

        return candidates.minByOrNull { it.distance }
    }
}
