package dev.shiftsad.statistics.events

import dev.shiftsad.statistics.add
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class CropHarvestedEvent : Listener {

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    fun onCropHarvestedEvent(event: BlockBreakEvent) {
        val block = event.block
        val player = event.player

        if (block !is Ageable) return
        val ageable = block as Ageable
        val cropType = block.type

        if (ageable.age != ageable.maximumAge) return
        val cropName = cropType.name.lowercase()

        if (cropName !in listOf(
            "wheat",
            "carrots",
            "potatoes",
            "beetroots",
            "nether_wart",
        )) return

        player.add(cropName, 1)
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    fun onBlockHarvestEvent(event: BlockBreakEvent) {
        val block = event.block
        val player = event.player

        val name = block.type.name.lowercase()

        if (name !in listOf(
            "melon",
            "pumpkin",
        ))

        player.add(name, 1)
    }
}