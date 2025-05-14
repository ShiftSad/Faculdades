package dev.shiftsad.statistics

import com.google.gson.GsonBuilder
import com.mojang.util.InstantTypeAdapter
import dev.shiftsad.statistics.dto.StatisticEventSnapshotDto
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.concurrent.TimeUnit

class StatisticsApiClient(
    private val baseUrl: String,
    private val apiKey: String,
) {

    private val logger = LoggerFactory.getLogger(StatisticsApiClient::class.java)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
        .create()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()

    fun sendSnapshot(snapshot: StatisticEventSnapshotDto) {
        logger.info("Sending snapshot to API: $snapshot")
        val jsonPayload = gson.toJson(snapshot)

        val request = Request.Builder()
            .url("$baseUrl/statistics/ingest")
            .post(jsonPayload.toRequestBody(JSON_MEDIA_TYPE))
            .headers(Headers.headersOf(
                "Content-Type", "application/json",
                "Authorization", "Bearer $apiKey"
            ))
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                logger.error("Error sending snapshot, {}", response)
            }
            val responseBody = response.body?.string()
            if (responseBody != null) logger.info("Response: $responseBody")
            else logger.error("Error: Response body is null")
        }
    }
}