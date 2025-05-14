package dev.shiftsad.statistics

import dev.shiftsad.statistics.dto.CropsHarvestedDto
import dev.shiftsad.statistics.dto.StatisticEventDto
import dev.shiftsad.statistics.dto.StatisticEventSnapshotDto
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.slf4j.LoggerFactory
import java.time.Instant

class PlayerStatsCollector {

    private val logger = LoggerFactory.getLogger(PlayerStatsCollector::class.java)

    fun collectSnapshot(player: Player): StatisticEventSnapshotDto {
        val playerId = player.uniqueId.toString()
        val timestamp = Instant.now()

        val generalStats = gatherGeneralStats(player)
        val cropsStats = gatherCropsHarvestedStats(player)

        val eventStats = StatisticEventDto(
            timePlayedSeconds = generalStats[Statistic.PLAY_ONE_MINUTE]?.div(20) ?: 0,
            playersKilled = generalStats[Statistic.PLAYER_KILLS] ?: 0,
            deaths = generalStats[Statistic.DEATHS] ?: 0,
            fishCaught = generalStats[Statistic.FISH_CAUGHT] ?: 0,
            animalsBred = generalStats[Statistic.ANIMALS_BRED] ?: 0,
            mobKills = generalStats[Statistic.MOB_KILLS] ?: 0,
            blocksMined = generalStats[Statistic.MINE_BLOCK] ?: 0,
            itemsCrafted = generalStats[Statistic.CRAFT_ITEM] ?: 0,
            walkOneCm = generalStats[Statistic.WALK_ONE_CM] ?: 0,
            jump = generalStats[Statistic.JUMP] ?: 0,
            sprintOneCm = generalStats[Statistic.SPRINT_ONE_CM] ?: 0,
            crouchOneCm = generalStats[Statistic.CROUCH_ONE_CM] ?: 0,
            fallOneCm = generalStats[Statistic.FALL_ONE_CM] ?: 0,
            swimOneCm = generalStats[Statistic.SWIM_ONE_CM] ?: 0,
            flyOneCm = generalStats[Statistic.FLY_ONE_CM] ?: 0,
            climbOneCm = generalStats[Statistic.CLIMB_ONE_CM] ?: 0,
            useItem = generalStats[Statistic.USE_ITEM] ?: 0,
            breakItem = generalStats[Statistic.BREAK_ITEM] ?: 0,
            talkedToVillager = generalStats[Statistic.TALKED_TO_VILLAGER] ?: 0,
            tradedWithVillager = generalStats[Statistic.TRADED_WITH_VILLAGER] ?: 0,
            cropsHarvested = cropsStats,
        )

        return StatisticEventSnapshotDto(
            playerId = playerId,
            timestamp = timestamp,
            stats = eventStats
        )
    }

    private fun gatherGeneralStats(player: Player): Map<Statistic, Int> {
        val stats = mutableMapOf<Statistic, Int>()

        val generalStatsToCollect = listOf(
            Statistic.PLAY_ONE_MINUTE,
            Statistic.PLAYER_KILLS,
            Statistic.DEATHS,
            Statistic.WALK_ONE_CM,
            Statistic.JUMP,
            Statistic.SPRINT_ONE_CM,
            Statistic.CROUCH_ONE_CM,
            Statistic.FALL_ONE_CM,
            Statistic.SWIM_ONE_CM,
            Statistic.FLY_ONE_CM,
            Statistic.CLIMB_ONE_CM,
            Statistic.MINE_BLOCK,
            Statistic.USE_ITEM,
            Statistic.BREAK_ITEM,
            Statistic.CRAFT_ITEM,
            Statistic.FISH_CAUGHT,
            Statistic.ANIMALS_BRED,
            Statistic.MOB_KILLS,
            Statistic.TALKED_TO_VILLAGER,
            Statistic.TRADED_WITH_VILLAGER
        )

        for (stat in generalStatsToCollect) {
            try {
                stats[stat] = player.getStatistic(stat)
            } catch (e: Exception) {
                logger.debug("Could not get statistic {} for player {}: {}", stat, player.name, e.message)
            }
        }
        return stats
    }

    private fun gatherCropsHarvestedStats(player: Player): CropsHarvestedDto {
        return CropsHarvestedDto(
            wheat = player.read("wheat", PersistentDataType.INTEGER) as Int? ?: 0,
            carrots = player.read("carrots", PersistentDataType.INTEGER) as Int? ?: 0,
            potatoes = player.read("potatoes", PersistentDataType.INTEGER) as Int? ?: 0,
            beetroot = player.read("beetroot", PersistentDataType.INTEGER) as Int? ?: 0,
            netherWart = player.read("nether_wart", PersistentDataType.INTEGER) as Int? ?: 0,
            melon = player.read("melon", PersistentDataType.INTEGER) as Int? ?: 0,
            pumpkin = player.read("pumpkin", PersistentDataType.INTEGER) as Int? ?: 0
        )
    }
}