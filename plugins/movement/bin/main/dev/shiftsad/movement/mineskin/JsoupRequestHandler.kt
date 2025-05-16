package dev.shiftsad.movement.mineskin

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.mineskin.MineSkinClientImpl
import org.mineskin.data.CodeAndMessage
import org.mineskin.exception.MineSkinRequestException
import org.mineskin.exception.MineskinException
import org.mineskin.request.RequestHandler
import org.mineskin.response.MineSkinResponse
import org.mineskin.response.ResponseConstructor
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.function.Function
import java.util.logging.Level
import java.util.stream.Collectors

class JsoupRequestHandler(
    private val userAgent: String, private val apiKey: String?,
    private val timeout: Int,
    gson: Gson?
) : RequestHandler(userAgent, apiKey, timeout, gson) {
    private fun requestBase(method: Connection.Method, url: String): Connection {
        MineSkinClientImpl.LOGGER.log(Level.FINE, "$method $url")
        val connection: Connection = Jsoup.connect(url)
            .method(method)
            .userAgent(userAgent)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .timeout(timeout)
        if (apiKey != null) {
            connection.header("Authorization", "Bearer $apiKey")
        }
        return connection
    }

    private fun <T, R : MineSkinResponse<T?>?> wrapResponse(response: Connection.Response, clazz: Class<T?>?, constructor: ResponseConstructor<T?, R?>): R? {
        try {
            val jsonBody: JsonObject? = gson.fromJson(response.body(), JsonObject::class.java)
            val wrapped = constructor.construct(
                response.statusCode(),
                lowercaseHeaders(response.headers().toMutableMap()),
                jsonBody,
                gson, clazz
            )
            if (!wrapped!!.isSuccess) {
                throw MineSkinRequestException(
                    wrapped.firstError.map<String?>(Function { obj: CodeAndMessage? -> obj!!.code() }).orElse("request_failed"),
                    wrapped.firstError.map<String?>(Function { obj: CodeAndMessage? -> obj!!.message() }).orElse("Request Failed"),
                    wrapped
                )
            }
            return wrapped
        } catch (e: JsonParseException) {
            MineSkinClientImpl.LOGGER.log(Level.WARNING, "Failed to parse response body: " + response.body(), e)
            throw MineskinException("Failed to parse response", e)
        }
    }

    private fun lowercaseHeaders(headers: MutableMap<String?, String?>): MutableMap<String?, String?> {
        return headers.entries.stream()
            .collect(Collectors.toMap(Function { e: MutableMap.MutableEntry<String?, String?>? -> e!!.key!!.lowercase(Locale.getDefault()) }, Function { it.value }))
    }

    @Throws(IOException::class)
    override fun <T, R : MineSkinResponse<T?>?> getJson(url: String, clazz: Class<T?>?, constructor: ResponseConstructor<T?, R?>): R? {
        val response: Connection.Response = requestBase(Connection.Method.GET, url).execute()
        return wrapResponse<T?, R?>(response, clazz, constructor)
    }

    @Throws(IOException::class)
    override fun <T, R : MineSkinResponse<T?>?> postJson(url: String, data: JsonObject, clazz: Class<T?>?, constructor: ResponseConstructor<T?, R?>): R? {
        val response: Connection.Response = requestBase(Connection.Method.POST, url)
            .requestBody(data.toString())
            .header("Content-Type", "application/json")
            .execute()
        return wrapResponse<T?, R?>(response, clazz, constructor)
    }

    @Throws(IOException::class)
    override fun <T, R : MineSkinResponse<T?>?> postFormDataFile(
        url: String,
        key: String?, filename: String?, `in`: InputStream?,
        data: MutableMap<String, String>?,
        clazz: Class<T?>?, constructor: ResponseConstructor<T?, R?>
    ): R? {
        val connection: Connection = requestBase(Connection.Method.POST, url)
            .header("Content-Type", "multipart/form-data")
        connection.data(key!!, filename!!, `in`!!)
        connection.data(data!!)
        val response: Connection.Response = connection.execute()
        return wrapResponse<T?, R?>(response, clazz, constructor)
    }
}