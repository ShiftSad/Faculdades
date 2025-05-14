package dev.shiftsad.statistics.dto

import java.time.Instant

data class CropsHarvestedDto(
    val wheat: Int? = null,
    val carrots: Int? = null,
    val potatoes: Int? = null,
    val beetroot: Int? = null,
    val netherWart: Int? = null,
    val melon: Int? = null,
    val pumpkin: Int? = null,
)

data class StatisticEventDto(
    val timePlayedSeconds: Int = 0,
    val blocksMined: Int = 0,
    val playersKilled: Int = 0,
    val deaths: Int = 0,
    val itemsCrafted: Int = 0,
    val fishCaught: Int = 0,
    val animalsBred: Int = 0,
    val mobKills: Int = 0,
    val walkOneCm: Int = 0,
    val jump: Int = 0,
    val sprintOneCm: Int = 0,
    val crouchOneCm: Int = 0,
    val fallOneCm: Int = 0,
    val swimOneCm: Int = 0,
    val flyOneCm: Int = 0,
    val climbOneCm: Int = 0,
    val useItem: Int = 0,
    val breakItem: Int = 0,
    val talkedToVillager: Int = 0,
    val tradedWithVillager: Int = 0,
    val cropsHarvested: CropsHarvestedDto,
)

data class StatisticEventSnapshotDto(
    val playerId: String,
    val timestamp: Instant,
    val stats: StatisticEventDto
)
