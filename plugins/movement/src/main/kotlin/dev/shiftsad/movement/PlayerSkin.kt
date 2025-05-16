package dev.shiftsad.movement

import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.shiftsad.movement.mineskin.PlayerTextures
import java.net.URI
import java.util.concurrent.CompletableFuture

object PlayerSkin {

    fun fromUsername(username: String): CompletableFuture<PlayerTextures?> {
        return jsonFromUsername(username)
            .thenCompose { json ->
                val uuid = json.get("id").asString
                fromUuid(uuid)
            }
    }

    fun fromUuid(uuid: String): CompletableFuture<PlayerTextures?> {
        return jsonFromUuid(uuid)
            .thenCompose { json ->
                val properties = json.get("properties").getAsJsonArray()
                for (jsonElement in properties) {
                    val propertyObject: JsonObject = jsonElement.getAsJsonObject()
                    val name = propertyObject.get("name").asString
                    if (name == "textures") {
                        val textureValue = propertyObject.get("value").asString
                        val signatureValue = propertyObject.get("signature").asString
                        return@thenCompose CompletableFuture.completedFuture(PlayerTextures(textureValue, signatureValue))
                    }
                }

                return@thenCompose null
            }
    }

    private fun jsonFromUrl(url: String): CompletableFuture<JsonObject> {
        return CompletableFuture.supplyAsync {
            val connection = URI(url).toURL().openConnection()
            connection.connect()
            val text = connection.getInputStream().bufferedReader().use { it.readText() }
            val json = Gson().fromJson(text, JsonObject::class.java)
            if (json.has("error")) {
                throw IllegalArgumentException("User not found")
            }

            json
        }
    }

    private fun jsonFromUsername(username: String): CompletableFuture<JsonObject> {
        val url = "https://api.mojang.com/users/profiles/minecraft/$username"
        return jsonFromUrl(url)
    }

    private fun jsonFromUuid(uuid: String): CompletableFuture<JsonObject> {
        val url = "https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false"
        return jsonFromUrl(url)
    }
}