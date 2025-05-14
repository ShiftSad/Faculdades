package dev.shiftsad.statistics

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class DataUploader(
    plugin: JavaPlugin,
    delay: Long,
    private val apiClient: StatisticsApiClient,
) {

    private val dataCollector = PlayerStatsCollector()
    private val task: BukkitTask

    init {
        task = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, uploadData(), 0L, delay)
    }

    fun uploadData() = Runnable {
        Bukkit.getOnlinePlayers().forEach {
            val data = dataCollector.collectSnapshot(it)
            apiClient.sendSnapshot(data)
        }
    }

    fun cancel() {
        task.cancel()
    }
}