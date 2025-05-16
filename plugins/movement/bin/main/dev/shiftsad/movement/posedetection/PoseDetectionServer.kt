package dev.shiftsad.movement.posedetection

import com.sun.net.httpserver.HttpServer
import dev.shiftsad.movement.Movement.Companion.plugin
import dev.shiftsad.movement.display.Pose
import dev.shiftsad.movement.display.Vec
import org.bukkit.Bukkit
import org.bukkit.util.Vector
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.io.File
import java.net.InetSocketAddress
import java.net.URI
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.*

object PoseDetectionServer {
    private val index = File("index.html")

    private const val SITE_PORT = 80
    private const val SITE_URL = "http://localhost:${SITE_PORT}"
    private const val WEBSOCKET_PORT = 8080
    private const val API_URL = "ws://localhost:${WEBSOCKET_PORT}"

    private const val ENDPOINT = "poseDetection"

    private const val DATA_BYTES = 4 * Pose.LANDMARK_COUNT * 3 * 2

    private val variables: Map<String, String> = mapOf(
        "siteUrl" to SITE_URL,
        "apiUrl" to API_URL,
        "dataBytes" to DATA_BYTES.toString(),
        "landmarkCount" to Pose.LANDMARK_COUNT.toString()
    )

    private val poseConsumers = HashMap<Token, (pose: Pose) -> Unit>()

    private val httpServer = HttpServer.create(InetSocketAddress("0.0.0.0", SITE_PORT), 0).apply {
        createContext("/$ENDPOINT") { exchange ->
            var text = index.readText()
            for ((varName, varValue) in variables) {
                text = text.replace("$$varName", varValue)
            }
            val content = text.toByteArray()
            exchange.sendResponseHeaders(200, content.size.toLong())
            exchange.responseBody.write(content)
            exchange.responseBody.close()
        }
    }

    private val websocket = object : WebSocketServer(InetSocketAddress("0.0.0.0", WEBSOCKET_PORT)) {
        override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
            runCatching { UUID.fromString(handshake.resourceDescriptor.substringAfter("token=")) }
                .onFailure { conn.close() }
                .onSuccess { conn.setAttachment(it) }
        }

        override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) = Unit

        override fun onMessage(conn: WebSocket, message: String) {
            if ("ping" == message) conn.send("pong")
        }

        override fun onMessage(conn: WebSocket, buffer: ByteBuffer) {
            if (buffer.limit() != DATA_BYTES) return
            if (!poseConsumers.containsKey(conn.getAttachment())) return
            val floatBuffer = buffer.asFloatBuffer()
            val normalizedLandmarks = (0..<33).map { floatBuffer.getVec() }
            val worldLandmarks = (0..<33).map { floatBuffer.getVec() }
            val pose = Pose(normalizedLandmarks, worldLandmarks)
            poseConsumers.getValue(conn.getAttachment()).invoke(pose)
        }

        override fun onError(conn: WebSocket?, ex: Exception) = Unit

        override fun onStart() = Unit

        private fun FloatBuffer.getVec() = Vec(-get().toDouble(), -get().toDouble(), get().toDouble())
    }

    fun createEndpoint(onPose: (Pose) -> Unit): URI {
        val token = UUID.randomUUID()
        poseConsumers[token] = onPose
        return URI("$SITE_URL/$ENDPOINT?token=$token")
    }

    fun start() {
        httpServer.start()
        websocket.start()
    }

    fun stop() {
        httpServer.stop(0)
        websocket.stop()
    }
}

typealias Token = UUID
