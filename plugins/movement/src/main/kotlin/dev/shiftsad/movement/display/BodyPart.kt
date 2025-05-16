package dev.shiftsad.movement.display

import com.destroystokyo.paper.profile.ProfileProperty
import dev.shiftsad.movement.Movement.Companion.plugin
import dev.shiftsad.movement.mineskin.SkinApi
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.Quaternionf
import org.joml.Vector3f
import java.awt.image.BufferedImage
import java.util.UUID
import java.util.concurrent.CompletableFuture

class BodyPart(
    image: BufferedImage,
    private val location: Location,
    vararg sections: BodyPartSection,
) {
    companion object {
        private fun headEntityFromBufferedImage(image: BufferedImage, location: Location): CompletableFuture<ItemDisplay> {
            val future = CompletableFuture<ItemDisplay>()
            Bukkit.getScheduler().runTask(plugin, Runnable {
                val entity = location.world.spawn(location, ItemDisplay::class.java)

                val head = ItemStack(Material.PLAYER_HEAD)
                val meta = head.itemMeta as SkullMeta
                val profile = SkinApi.generate(image).get()
                meta.playerProfile = Bukkit.createProfile(UUID.randomUUID(), null).apply {
                    setProperty(ProfileProperty("textures", profile.value, profile.signature))
                }
                head.itemMeta = meta

                entity.setItemStack(head)
                entity.setGravity(false)
                entity.interpolationDuration = 1
                entity.interpolationDelay = 0

                future.complete(entity)
            })
            return future
        }

        private fun createCustomHeadSkinImage(
            source: BufferedImage,
            imageSource: BodyPartImageSource
        ) = BufferedImage(source.width, source.height, source.type).also { result ->
            listOf(
                imageSource.front to Rect(8, 8, 8, 8),
                imageSource.left to Rect(0, 8, 8, 8),
                imageSource.back to Rect(24, 8, 8, 8),
                imageSource.right to Rect(16, 8, 8, 8),
                imageSource.bottom to Rect(16, 0, 8, 8),
                imageSource.top to Rect(8, 0, 8, 8)
            ).forEach { (sourceRegion, targetRegion) ->
                if (sourceRegion == null) return@forEach
                copyRegion(source, result, sourceRegion, targetRegion)
                copyRegion(source, result, sourceRegion.translated(imageSource.outerLayerOffset.x, imageSource.outerLayerOffset.y), targetRegion.translated(32, 0))
            }
        }

        private fun copyRegion(source: BufferedImage, target: BufferedImage, sourceRegion: Rect, targetRegion: Rect) {
            for (y in 0..<targetRegion.height) {
                for (x in 0..<targetRegion.width) {
                    val targetX = targetRegion.x + x
                    val targetY = targetRegion.y + y
                    val sourceX = sourceRegion.x + ((x / targetRegion.width.toDouble()) * sourceRegion.width).toInt()
                    val sourceY = sourceRegion.y + ((y / targetRegion.height.toDouble()) * sourceRegion.height).toInt()
                    target.setRGB(targetX, targetY, source.getRGB(sourceX, sourceY))
                }
            }
        }
    }

    internal val bodyPartEntities: List<BodyPartEntity> = sections.map { (imageSource, transform) ->
        val headSkinImage = createCustomHeadSkinImage(image, imageSource)
        val entity = headEntityFromBufferedImage(headSkinImage, location).get()

        val scale = transform.scale
        val translation = transform.translation

        entity.transformation = Transformation(
            Vector3f(translation.x.toFloat(), translation.y.toFloat(), translation.z.toFloat()),
            Quaternionf(),
            Vector3f(scale.x.toFloat(), scale.y.toFloat(), scale.z.toFloat()),
            Quaternionf()
        )

        BodyPartEntity(entity, transform)
    }

    fun spawn(location: Location) {
        for ((entity, _) in bodyPartEntities) {
            entity.teleport(location)
        }
    }

    fun teleport(location: Location) {
        for ((entity, _) in bodyPartEntities) {
            entity.teleport(location)
        }
    }

    fun remove() {
        for ((entity, _) in bodyPartEntities) {
            entity.remove()
        }
    }

    fun rotate(quaternion: Quaternionf) {
        for ((entity, transform) in bodyPartEntities) {
            val currentTransform = entity.transformation
            val translationVector = quaternion.transform(Vector3f(
                transform.translation.x.toFloat(),
                transform.translation.y.toFloat(),
                transform.translation.z.toFloat()
            ))

            entity.transformation = Transformation(
                translationVector,
                quaternion,
                currentTransform.scale,
                currentTransform.rightRotation
            )
        }
    }
}

data class BodyPartImageSource(
    val front: Rect?,
    val left: Rect?,
    val back: Rect?,
    val right: Rect?,
    val bottom: Rect?,
    val top: Rect?,
    val outerLayerOffset: Vec2I
)

data class BodyPartTransform(val scale: Vector, val translation: Vector = Vector(0, 0, 0))

data class BodyPartEntity(val entity: ItemDisplay, val transform: BodyPartTransform)

data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) {
    fun translated(deltaX: Int, deltaY: Int) = Rect(x + deltaX, y + deltaY, width, height)
}

data class Vec2I(val x: Int, val y: Int)