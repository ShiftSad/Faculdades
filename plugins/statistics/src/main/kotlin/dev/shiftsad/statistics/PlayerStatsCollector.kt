package dev.shiftsad.statistics

import dev.shiftsad.statistics.dto.StatisticEventDto
import dev.shiftsad.statistics.dto.StatisticEventSnapshotDto
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.slf4j.Logger
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
            timePlayedSeconds = generalStats[Statistic.PLAY_ONE_MINUTE]?.div(20),
            playersKilled = generalStats[Statistic.PLAYER_KILLS],
            deaths = generalStats[Statistic.DEATHS],
            fishCaught = generalStats[Statistic.FISH_CAUGHT],
            animalsBred = generalStats[Statistic.ANIMALS_BRED],
            mobKills = generalStats[Statistic.MOB_KILLS],
            blocksMined = generalStats[Statistic.MINE_BLOCK],
            itemsCrafted = generalStats[Statistic.CRAFT_ITEM],
            walkOneCm = generalStats[Statistic.WALK_ONE_CM],
            jump = generalStats[Statistic.JUMP],
            sprintOneCm = generalStats[Statistic.SPRINT_ONE_CM],
            crouchOneCm = generalStats[Statistic.CROUCH_ONE_CM],
            fallOneCm = generalStats[Statistic.FALL_ONE_CM],
            swimOneCm = generalStats[Statistic.SWIM_ONE_CM],
            flyOneCm = generalStats[Statistic.FLY_ONE_CM],
            climbOneCm = generalStats[Statistic.CLIMB_ONE_CM],
            useItem = generalStats[Statistic.USE_ITEM],
            breakItem = generalStats[Statistic.BREAK_ITEM],
            talkedToVillager = generalStats[Statistic.TALKED_TO_VILLAGER],
            tradedWithVillager = generalStats[Statistic.TRADED_WITH_VILLAGER],
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
}