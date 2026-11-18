package com.poolcompanion.app.engine

import com.poolcompanion.app.model.ShotRecord

object ShotStatisticsEngine {

    private val records = mutableListOf<ShotRecord>()

    fun addShot(record: ShotRecord) {
        records.add(record)
    }

    fun getTotalShots(): Int {
        return records.size
    }

    fun getAverageDistance(): Double {
        if (records.isEmpty()) return 0.0
        return records.map { it.distance }.average()
    }

    fun getAverageCutAngle(): Double {
        if (records.isEmpty()) return 0.0
        return records.map { it.cutAngle }.average()
    }

    fun getEasyShots(): Int {
        return records.count { it.difficulty == "EASY" }
    }

    fun getHardShots(): Int {
        return records.count {
            it.difficulty == "HARD" ||
            it.difficulty == "VERY HARD"
        }
    }

    fun getAllShots(): List<ShotRecord> {
        return records.toList()
    }

    fun clear() {
        records.clear()
    }
}
