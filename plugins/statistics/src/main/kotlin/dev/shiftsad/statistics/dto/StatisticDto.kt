package dev.shiftsad.statistics.dto

import java.time.Instant

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
    val fishCaught: Int? = null,
    val animalsBred: Int? = null,
    val mobKills: Int? = null,
    val walkOneCm: Int? = null,
    val jump: Int? = null,
    val sprintOneCm: Int? = null,
    val crouchOneCm: Int? = null,
    val fallOneCm: Int? = null,
    val swimOneCm: Int? = null,
    val flyOneCm: Int? = null,
    val climbOneCm: Int? = null,
    val useItem: Int? = null,
    val breakItem: Int? = null,
    val talkedToVillager: Int? = null,
    val tradedWithVillager: Int? = null,
    val cropsHarvested: CropsHarvestedDto? = null,
)

data class StatisticEventSnapshotDto(
    val playerId: String,
    val timestamp: Instant,
    val stats: StatisticEventDto
)
