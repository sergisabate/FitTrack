package com.sergisabate.fittrack.data.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.sergisabate.fittrack.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun isOpenAIConfigured(): Boolean = BuildConfig.OPENAI_API_KEY.isNotBlank()

suspend fun getGPTContent(
    systemMessage: String,
    userMessage: String
): String {
    val request = ChatGPTRequest(
        model = "gpt-4.1-mini",
        responseFormat = mapOf("type" to "json_object"),
        messages = listOf(
            Message("system", systemMessage),
            Message("user", userMessage)
        )
    )

    return withContext(Dispatchers.IO) {
        try {
            val response = chatGPTApi.getChatGPTResponse(request).execute()
            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content ?: ""
            } else {
                ""
            }
        } catch (_: Exception) {
            ""
        }
    }
}
