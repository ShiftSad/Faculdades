@file:Suppress("UnstableApiUsage")

package dev.shiftsad.movement

import dev.shiftsad.movement.posedetection.PoseDetectionServer
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin


class Movement : JavaPlugin() {

    companion object {
        lateinit var plugin: JavaPlugin
            private set
    }

    override fun onEnable() {
        plugin = this

        this.lifecycleManager.registerEventHandler<ReloadableRegistrarEvent<Commands>>(
            LifecycleEvents.COMMANDS,
            LifecycleEventHandler { commands: ReloadableRegistrarEvent<Commands> ->
                commands.registrar().register(
                    PoseCommand(this).createCommand().build()
                )
            })

        PoseDetectionServer.start()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
