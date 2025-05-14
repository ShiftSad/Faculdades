package dev.shiftsad.statistics

import org.bukkit.plugin.java.JavaPlugin

class StatisticsPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info { "Statistics enabled" }
    }
}