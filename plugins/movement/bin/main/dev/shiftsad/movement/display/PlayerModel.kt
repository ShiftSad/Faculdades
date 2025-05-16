package dev.shiftsad.movement.display

import dev.shiftsad.movement.Movement.Companion.plugin
import dev.shiftsad.movement.PlayerSkin
import dev.shiftsad.movement.display.leftHip
import dev.shiftsad.movement.mineskin.PlayerTextures
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.joml.Quaternionf
import org.joml.Vector3f
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.net.URI
import java.util.*
import javax.imageio.ImageIO
import kotlin.div
import kotlin.math.max

class PlayerModel(skin: PlayerTextures, private val player: Player) {
    private val slim: Boolean
    private val skinImage: BufferedImage

    init {
        val skinTextureData = Base64.getDecoder()
            .decode(skin.value)
            .let { String(it) }

        skinImage = skinTextureData
            .substringAfter("url\" : \"")
            .substringBefore("\"")
            .let { URI(it).toURL().readBytes() }
            .let { ImageIO.read(ByteArrayInputStream(it)) }

        slim = skinTextureData.substringAfter("\"model\"").substringBefore('}').contains("slim")
    }

    private val head = BodyPart(skinImage, player.location.apply { yaw = 0f; pitch = 0f }, BodyPartSection.HEAD)
    private val body = BodyPart(skinImage, player.location.apply { yaw = 0f; pitch = 0f }, BodyPartSection.UPPER_BODY, BodyPartSection.LOWER_BODY)
    private val leftArm = BodyPart(skinImage, player.location.apply { yaw = 0f; pitch = 0f }, BodyPartSection.UPPER_LEFT_ARM(slim), BodyPartSection.LOWER_LEFT_ARM(slim))
    private val rightArm = BodyPart(skinImage, player.location.apply { yaw = 0f; pitch = 0f }, BodyPartSection.UPPER_RIGHT_ARM(slim), BodyPartSection.LOWER_RIGHT_ARM(slim))
    private val leftLeg = BodyPart(skinImage, player.location.apply { yaw = 0f; pitch = 0f }, BodyPartSection.UPPER_LEFT_LEG, BodyPartSection.LOWER_LEFT_LEG)
    private val rightLeg = BodyPart(skinImage, player.location.apply { yaw = 0f; pitch = 0f }, BodyPartSection.UPPER_RIGHT_LEG, BodyPartSection.LOWER_RIGHT_LEG)

    fun forEachBodyPart(consumer: (BodyPart) -> Unit) {
        consumer(head)
        consumer(body)
        consumer(leftArm)
        consumer(rightArm)
        consumer(leftLeg)
        consumer(rightLeg)
    }

    fun spawn(location: Location) = forEachBodyPart { Bukkit.getScheduler().runTask(plugin, Runnable { it.spawn(location) }) }

    fun teleport(location: Location) {
        head.teleport(location.clone().add(0.0, 1.625, -0.25))
        body.teleport(location.clone().add(0.0, 1.375, 0.0))
        leftArm.teleport(location.clone().add(-0.344, 1.375, 0.0))
        rightArm.teleport(location.clone().add(0.344, 1.375, 0.0))
        leftLeg.teleport(location.clone().add(-0.125, 0.625, 0.0))
        rightLeg.teleport(location.clone().add(0.125, 0.625, 0.0))
    }

    fun remove() = forEachBodyPart { it.remove() }

    fun fromPose(pose: Pose) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val nLandmarks = pose.normalizedLandmarks
            val wLandmarks = pose.worldLandmarks

            val normalizedHipCenter = nLandmarks.leftHip().add(nLandmarks.rightHip()).div(2.0)
            val positionOffset = Vec((normalizedHipCenter.x + 0.5) * 10.0 + player.location.x, max(normalizedHipCenter.y * 4.0 + 3.1 + player.location.y, 0.2), 0.0 + player.location.z)
            val positionTransform = { pos: Vec -> pos.times(1.5).plus(positionOffset).asPos() }

            val faceCenter = wLandmarks.leftEyeInner().plus(wLandmarks.rightEyeInner()).div(2.0)
            val earCenter = wLandmarks.leftEar().plus(wLandmarks.rightEar()).div(2.0)
            val facing = earCenter.sub(faceCenter)
            head.teleport(positionTransform(faceCenter))
            head.rotate(
                rotation(
                    Vector3f(0f, 0f, 1f),
                    facing.toVector3f()
                )
            )

            val upperBodyCenter = wLandmarks.leftShoulder().plus(wLandmarks.rightShoulder()).div(2.0)
            val hipCenter = wLandmarks.leftHip().plus(wLandmarks.rightHip()).div(2.0)
            val leftShoulderToRightShoulder = wLandmarks.rightShoulder().sub(wLandmarks.leftShoulder())
            val rightHipToRightShoulder = wLandmarks.rightShoulder().sub(wLandmarks.rightHip())
            val bodyNormal = leftShoulderToRightShoulder.toVector3f().cross(rightHipToRightShoulder.toVector3f())
            body.teleport(positionTransform(upperBodyCenter))
            body.rotate(
                rotation(
                    Vector3f(0f, -1f, 0f),
                    hipCenter.sub(upperBodyCenter).toVector3f(),
                    Vector3f(0f, 0f, 1f),
                    bodyNormal
                )
            )

            val leftShoulder = wLandmarks.leftShoulder()
            leftArm.teleport(positionTransform(leftShoulder))
            leftArm.rotate(
                rotation(
                    Vector3f(0f, -1f, 0f),
                    wLandmarks.leftWrist().sub(leftShoulder).toVector3f()
                )
            )

            val rightShoulder = wLandmarks.rightShoulder()
            rightArm.teleport(positionTransform(rightShoulder))
            rightArm.rotate(
                rotation(
                    Vector3f(0f, -1f, 0f),
                    wLandmarks.rightWrist().sub(rightShoulder).toVector3f()
                )
            )

            val leftHip = wLandmarks.leftHip()
            leftLeg.teleport(positionTransform(leftHip))
            leftLeg.rotate(
                rotation(
                    Vector3f(0f, -1f, 0f),
                    wLandmarks.leftAnkle().sub(leftHip).toVector3f(),
                    Vector3f(0f, 0f, -1f),
                    wLandmarks.leftFootIndex().sub(leftHip).toVector3f()
                )
            )

            val rightHip = wLandmarks.rightHip()
            rightLeg.teleport(positionTransform(rightHip))
            rightLeg.rotate(
                rotation(
                    Vector3f(0f, -1f, 0f),
                    wLandmarks.rightAnkle().sub(rightHip).toVector3f(),
                    Vector3f(0f, 0f, -1f),
                    wLandmarks.rightFootIndex().sub(rightHip).toVector3f()
                )
            )
        })
    }

    private fun rotation(fromDirection: Vector3f, toDirection: Vector3f, fromNormal: Vector3f? = null, normalTarget: Vector3f? = null): Quaternionf {
        val directionQuaternionf = Quaternionf().rotationTo(fromDirection, toDirection)
        if (fromNormal == null || normalTarget == null) return directionQuaternionf

        val transformedNormal = Vector3f()
        directionQuaternionf.transform(fromNormal, transformedNormal)
        val idealNormal = getNormalOnLineThroughPoint(toDirection, normalTarget)

        val angle = transformedNormal.angle(idealNormal)
        directionQuaternionf.mul(Quaternionf().rotationY(angle))

        val testNormal = Vector3f()
        directionQuaternionf.transform(fromNormal, testNormal)
        if (testNormal.angle(idealNormal) > 0.5f) {
            directionQuaternionf.mul(Quaternionf().rotationY(-2 * angle))
        }

        return directionQuaternionf
    }

    private fun getNormalOnLineThroughPoint(lineDirection: Vector3f, point: Vector3f): Vector3f {
        val t = point.dot(lineDirection) / lineDirection.dot(lineDirection)
        val closestPoint = Vector3f(lineDirection).mul(t)
        return Vector3f(point).sub(closestPoint)
    }
}

data class Vec(
    val x: Double,
    val y: Double,
    val z: Double
) {
    fun times(scalar: Double) = Vec(x * scalar, y * scalar, z * scalar)
    fun plus(other: Vec) = Vec(x + other.x, y + other.y, z + other.z)
    fun sub(other: Vec) = Vec(x - other.x, y - other.y, z - other.z)
    fun asPos() = Location(Bukkit.getWorld("world"), x, y, z)
    fun div(scalar: Double) = Vec(x / scalar, y / scalar, z / scalar)
    fun add(other: Vec) = Vec(x + other.x, y + other.y, z + other.z)
    fun add(scalar: Double) = Vec(x + scalar, y + scalar, z + scalar)

    fun toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
}