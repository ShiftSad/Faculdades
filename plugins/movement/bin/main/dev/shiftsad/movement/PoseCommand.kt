@file:Suppress("UnstableApiUsage")

package dev.shiftsad.movement

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.shiftsad.movement.display.PlayerModel
import dev.shiftsad.movement.mineskin.PlayerTextures
import dev.shiftsad.movement.posedetection.PoseDetectionServer
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.Executors
import kotlin.time.measureTime

class PoseCommand(
    private val plugin: JavaPlugin
) {
    companion object {
        private val LINK_COLOR = TextColor.color(84, 149, 255)
    }

    fun createCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("pose")
            .then(
                Commands.argument("username", StringArgumentType.string())
                    .executes { context ->
                        val source = context.getSource().executor
                        val username = context.getArgument("username", String::class.java)

                        if (source !is Player) {
                            return@executes 1
                        }

                        PlayerSkin.fromUsername(username)
                            .thenAccept { skin ->
                                if (skin == null) {
                                    source.sendMessage(
                                        Component.text("Skin for user \"$username\" not found")
                                    )
                                } else {
                                    Bukkit.getScheduler().runTask(
                                        plugin,
                                        Runnable {
                                            createModel(skin, source)
                                        }
                                    )
                                }
                            }

                        return@executes 1
                    }
            )
    }

    private fun createModel(skin: PlayerTextures?, sender: Player) {
        runCatching {
            requireNotNull(skin)
            sender.sendMessage(Component.text("Loading your player model. This may take some time.", NamedTextColor.GRAY))

            val scheduler = Bukkit.getScheduler()

            scheduler.runTaskAsynchronously(plugin, Runnable {
                val model: PlayerModel
                measureTime { model = PlayerModel(skin, sender) }
                    .let { sender.sendMessage(Component.text("Finished loading your player model in ${it.inWholeSeconds}s ${it.inWholeMilliseconds % 1000}ms.", NamedTextColor.GRAY)) }

                scheduler.runTask(plugin, Runnable {
                    model.teleport(sender.location.apply { yaw = 0f; pitch = 0f })
                    val executor = Executors.newSingleThreadExecutor()
                    val uri = PoseDetectionServer.createEndpoint { pose ->
                        executor.submit {
                            model.fromPose(pose)
                        }
                    }

                    sender.sendMessage(
                        Component.text("You can now control your model ")
                            .append(
                                Component.text("using this link", LINK_COLOR, TextDecoration.UNDERLINED)
                                    .hoverEvent(HoverEvent.showText(Component.text(uri.toString(), LINK_COLOR, TextDecoration.UNDERLINED)))
                                    .clickEvent(ClickEvent.openUrl(uri.toURL()))
                            )
                            .append(Component.text("."))
                    )
                    sender.gameMode = GameMode.SPECTATOR
                    sender.isInvisible = true
                })
            })
        }.onFailure {
            it.printStackTrace()
            sender.sendMessage(Component.text("${it.cause}: ${it.message}", NamedTextColor.RED))
        }
    }
}
