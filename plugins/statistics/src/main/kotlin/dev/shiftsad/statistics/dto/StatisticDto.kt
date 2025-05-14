package dev.shiftsad.statistics.dto

import java.util.*

data class CropsHarvestedDto(
    val wheat: Int? = null,
    val carrots: Int? = null,
    val potatoes: Int? = null,
    val beetroot: Int? = null,
    val netherWart: Int? = null,
    val melon: Int? = null,
    val pumpkin: Int? = null
)

data class StatisticEventDto(
    val timePlayedSeconds: Int? = null,
    val blocksMined: Int? = null,
    val playersKilled: Int? = null,
    val deaths: Int? = null,
    val itemsCrafted: Int? = null,
    val distanceTraveledBlocks: Int? = null,
    val cropsHarvested: CropsHarvestedDto? = null
)

data class StatisticEventSnapshotDto(
    val playerId: String,
    val timestamp: Date,
    val stats: StatisticEventDto
)
