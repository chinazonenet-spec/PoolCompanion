package com.poolcompanion.app.engine

import kotlin.math.acos
import kotlin.math.hypot

data class PoolPocket(
    val name: String,
    val x: Double,
    val y: Double
)

data class PocketShot(
    val pocket: PoolPocket,
    val ghostX: Double,
    val ghostY: Double,
    val cueDistance: Double,
    val objectDistance: Double,
    val cutAngle: Double,
    val difficulty: String
)

object PocketEngine {

    val pockets = listOf(
        PoolPocket("TOP LEFT", 0.0, 0.0),
        PoolPocket("TOP MIDDLE", 500.0, 0.0),
        PoolPocket("TOP RIGHT", 1000.0, 0.0),
        PoolPocket("BOTTOM LEFT", 0.0, 500.0),
        PoolPocket("BOTTOM MIDDLE", 500.0, 500.0),
        PoolPocket("BOTTOM RIGHT", 1000.0, 500.0)
    )

    fun evaluate(
        cueX: Double,
        cueY: Double,
        targetX: Double,
        targetY: Double,
        pocketName: String,
        tableWidth: Double = 1000.0,
        tableHeight: Double = 500.0,
        ballRadius: Double = 12.0
    ): PocketShot? {

        val pocket = pockets.find {
            it.name == pocketName
        } ?: return null

        val objectDX = pocket.x - targetX
        val objectDY = pocket.y - targetY

        val objectDistance = hypot(objectDX, objectDY)

        if (objectDistance == 0.0) return null

        val ux = objectDX / objectDistance
        val uy = objectDY / objectDistance

        // Approximate ghost-ball center
        val ghostX = targetX - ux * (2.0 * ballRadius)
        val ghostY = targetY - uy * (2.0 * ballRadius)

        if (ghostX !in 0.0..tableWidth ||
            ghostY !in 0.0..tableHeight) return null

        val cueDX = ghostX - cueX
        val cueDY = ghostY - cueY

        val cueDistance = hypot(cueDX, cueDY)

        if (cueDistance == 0.0) return null

        val dot = (
            (cueDX / cueDistance) * ux +
            (cueDY / cueDistance) * uy
        ).coerceIn(-1.0, 1.0)

        val cutAngle = Math.toDegrees(acos(dot))

        val difficulty = when {
            cutAngle <= 15.0 -> "EASY"
            cutAngle <= 30.0 -> "MODERATE"
            cutAngle <= 50.0 -> "HARD"
            else -> "VERY HARD"
        }

        return PocketShot(
            pocket = pocket,
            ghostX = ghostX,
            ghostY = ghostY,
            cueDistance = cueDistance,
            objectDistance = objectDistance,
            cutAngle = cutAngle,
            difficulty = difficulty
        )
    }
}
