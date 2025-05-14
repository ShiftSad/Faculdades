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

    private val task: BukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
        Bukkit.getOnlinePlayers().forEach {
            if (!it.isOnline) return@forEach
            print("Uploading data for ${it.name}")
            val data = dataCollector.collectSnapshot(it)
            apiClient.sendSnapshot(data)
        }
    }, delay, delay)

    fun cancel() {
        task.cancel()
    }
}