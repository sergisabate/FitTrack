package com.sergisabate.fittrack.data.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

fun getGPTContent(
    systemMessage: String,
    userMessage: String,
    onResult: (String) -> Unit
) {
    val request = ChatGPTRequest(
        model = "gpt-4.1-mini",
        responseFormat = mapOf("type" to "json_object"),
        messages = listOf(
            Message("system", systemMessage),
            Message("user", userMessage)
        )
    )

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = chatGPTApi.getChatGPTResponse(request).execute()
            if (response.isSuccessful) {
                val menu = response.body()?.choices?.get(0)?.message?.content ?: ""
                onResult(menu)
            } else {
                onResult("")
            }
        } catch (_: Exception) {
            onResult("")
        }
    }
}
