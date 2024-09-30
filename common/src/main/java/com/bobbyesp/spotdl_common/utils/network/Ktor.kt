package com.bobbyesp.spotdl_common.utils.network

import com.bobbyesp.spotdl_common.utils.json
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import kotlinx.serialization.SerializationException

object Ktor {
    val client = HttpClient(    Android.create {
        connectTimeout = 10_000 //ms
        socketTimeout = 10_000 //ms
    }) {
        //Here we can add proxy support and more things
    }

    @Throws(Exception::class)
    suspend inline fun <reified T> get(
        client: HttpClient = this.client,
        url: String,
        params: Map<String, String>?
    ): T {
        val response: String = client.get(url) {
            url {
                params?.forEach { (key, value) ->
                    parameters.append(key, value)
                }
            }
        }.body()

        return try {
            json.decodeFromString<T>(response)
        } catch (e: SerializationException) {
            throw SerializationException("Something bad happened while trying to deserialize the response: \n $response", e)
        } catch (e: Exception) {
            throw Exception("An unknown error occurred while trying to deserialize the response: \n $response", e)
        }
    }
}