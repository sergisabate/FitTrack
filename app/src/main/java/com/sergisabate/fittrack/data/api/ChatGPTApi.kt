package com.sergisabate.fittrack.data.api

import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

const val API_KEY = "REDACTED_OPENAI_KEY"

class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $apiKey")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

data class ChatGPTRequest(
    val model: String,
    val messages: List<Message>,
    @SerializedName("response_format")
    val responseFormat: Map<String, String>
)

data class ChatGPTResponse(val choices: List<Choice>)

data class Choice(val message: Message)
data class Message(val role: String, val content: String)

val client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor(API_KEY))
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.openai.com/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface ChatGPTApi {
    @POST("v1/chat/completions")
    fun getChatGPTResponse(@Body request: ChatGPTRequest): Call<ChatGPTResponse>
}

val chatGPTApi: ChatGPTApi = retrofit.create(ChatGPTApi::class.java)

