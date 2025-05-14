package dev.shiftsad.statistics

import dev.shiftsad.statistics.events.CropHarvestedEvent
import org.bukkit.plugin.java.JavaPlugin

class StatisticsPlugin : JavaPlugin() {

    private lateinit var apiClient: StatisticsApiClient
    private lateinit var dataUploader: DataUploader

    override fun onEnable() {
        saveDefaultConfig()

        apiClient = StatisticsApiClient(
            baseUrl = config.getString("api.base_url") ?: "https://api.shiftsad.dev",
            apiKey = config.getString("api.key") ?: "banana",
        )

        dataUploader = DataUploader(
            plugin = this,
            apiClient = apiClient,
            delay = config.getLong("api.delay", 20L * 5L * 60L),
        )

        server.pluginManager.registerEvents(CropHarvestedEvent(), this)
        logger.info { "Statistics enabled" }
    }

    override fun onDisable() {
        dataUploader.cancel()
        logger.info { "Statistics disabled" }
    }
}