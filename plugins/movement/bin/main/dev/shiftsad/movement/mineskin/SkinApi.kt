package dev.shiftsad.movement.mineskin

import org.bukkit.entity.Player
import org.mineskin.MineSkinClient
import org.mineskin.data.Visibility
import org.mineskin.request.GenerateRequest
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.CompletableFuture
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

class SkinApi {
    companion object {
        private val API_KEY = runCatching { File("mineSkinApiKey").readText() }
            .onFailure { throw IllegalStateException("Please provide your mineskin api key (https://account.mineskin.org/keys) in a file called \"mineSkinApiKey\"") }
            .getOrThrow()

        private val client = MineSkinClient.builder()
            .requestHandler(::JsoupRequestHandler)
            .userAgent("WolfiiPaperProject/v0.0.1")
            .apiKey(API_KEY)
            .build()

        private val cachePath = Path("./skinCache").also { it.createDirectories() }

        fun generate(image: BufferedImage): CompletableFuture<PlayerTextures> {
            val hash = image.hash()
            val file = cachePath.resolve(hash).toFile()
            if (file.exists()) {
                val (value, signature) = file.readLines()
                return CompletableFuture.completedFuture(PlayerTextures(value, signature))
            }

            return CompletableFuture.supplyAsync {
                val request = GenerateRequest.upload(image)
                    .name(Math.random().toString())
                    .visibility(Visibility.UNLISTED)

                val data = client.queue().submit(request)
                    .thenCompose { it.job.waitForCompletion(client) }
                    .thenCompose { it.getOrLoadSkin(client) }
                    .get().texture().data

                if (!file.exists()) {
                    file.createNewFile()
                    file.writeText(data.value + "\n" + data.signature + "\n")
                }
                return@supplyAsync PlayerTextures(data.value, data.signature)
            }
        }

        private fun BufferedImage.hash() =
            ((getRGB(0, 0, width / 2, height, null, 0, width).contentHashCode().toULong() shl 32) or
                    getRGB(width / 2, 0, width / 2, height, null, 0, width).contentHashCode().toULong()).toString(16)
    }
}

data class PlayerTextures(val value: String, val signature: String)
