package dev.shiftsad.statistics

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class DataUploader(
    plugin: JavaPlugin,
) {

    private val dataCollector = PlayerStatsCollector()

    init {
        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, uploadData(), 0L, 20L * 60L * 5L)
    }

    fun uploadData() = Runnable {
        Bukkit.getOnlinePlayers().forEach {
            val data = dataCollector.collectSnapshot(it)
            
        }
    }
}