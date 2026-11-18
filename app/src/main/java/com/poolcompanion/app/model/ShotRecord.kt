package com.poolcompanion.app.model

data class ShotRecord(
    val angle: Double,
    val distance: Double,
    val cutAngle: Double,
    val difficulty: String,
    val pocket: String,
    val timestamp: Long = System.currentTimeMillis()
)
