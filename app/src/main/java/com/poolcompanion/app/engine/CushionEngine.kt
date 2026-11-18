package com.poolcompanion.app.engine

import kotlin.math.hypot

data class CushionResult(
    val bounceX: Double,
    val bounceY: Double,
    val finalX: Double,
    val finalY: Double,
    val cushion: String,
    val distance: Double
)

object CushionEngine {

    fun predict(
        startX: Double,
        startY: Double,
        targetX: Double,
        targetY: Double,
        tableWidth: Double = 1000.0,
        tableHeight: Double = 500.0
    ): CushionResult? {

        if (tableWidth <= 0.0 || tableHeight <= 0.0) return null

        if (startX !in 0.0..tableWidth ||
            targetX !in 0.0..tableWidth ||
            startY !in 0.0..tableHeight ||
            targetY !in 0.0..tableHeight) return null

        if (startX == targetX && startY == targetY) return null

        data class Candidate(
            val result: CushionResult
        )

        val candidates = mutableListOf<Candidate>()

        fun testWall(
            mirroredX: Double,
            mirroredY: Double,
            wall: String
        ) {
            val dx = mirroredX - startX
            val dy = mirroredY - startY

            val t = when (wall) {
                "LEFT" -> {
                    if (dx == 0.0) return
                    -startX / dx
                }
                "RIGHT" -> {
                    if (dx == 0.0) return
                    (tableWidth - startX) / dx
                }
                "TOP" -> {
                    if (dy == 0.0) return
                    -startY / dy
                }
                "BOTTOM" -> {
                    if (dy == 0.0) return
                    (tableHeight - startY) / dy
                }
                else -> return
            }

            if (t <= 0.0 || t >= 1.0) return

            val bx = startX + t * dx
            val by = startY + t * dy

            if (bx !in 0.0..tableWidth ||
                by !in 0.0..tableHeight) return

            val distance = hypot(dx, dy)

            candidates.add(
                Candidate(
                    CushionResult(
                        bounceX = bx,
                        bounceY = by,
                        finalX = targetX,
                        finalY = targetY,
                        cushion = wall,
                        distance = distance
                    )
                )
            )
        }

        testWall(-targetX, targetY, "LEFT")
        testWall(2.0 * tableWidth - targetX, targetY, "RIGHT")
        testWall(targetX, -targetY, "TOP")
        testWall(targetX, 2.0 * tableHeight - targetY, "BOTTOM")

        return candidates.minByOrNull {
            it.result.distance
        }?.result
    }
}
