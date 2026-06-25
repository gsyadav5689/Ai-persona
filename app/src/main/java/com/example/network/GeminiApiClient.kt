package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseFormat: ResponseFormat? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val responseModalities: List<String>? = null,
    val imageConfig: ImageConfig? = null
)

@JsonClass(generateAdapter = true)
data class ResponseFormat(
    val responseMimeType: String
)

@JsonClass(generateAdapter = true)
data class ImageConfig(
    val aspectRatio: String,
    val imageSize: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null,
    val promptFeedback: PromptFeedback? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class PromptFeedback(
    val blockReason: String? = null
)

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse

    @POST("v1beta/models/{model}:predict")
    suspend fun predictImagen(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: ImagenPredictRequest
    ): ImagenPredictResponse
}

@JsonClass(generateAdapter = true)
data class ImagenPredictRequest(
    val instances: List<ImagenInstance>,
    val parameters: ImagenParameters? = null
)

@JsonClass(generateAdapter = true)
data class ImagenInstance(
    val prompt: String
)

@JsonClass(generateAdapter = true)
data class ImagenParameters(
    val numberOfImages: Int? = 1,
    val outputMimeType: String? = "image/jpeg",
    val aspectRatio: String? = "1:1",
    val sampleCount: Int? = null
)

@JsonClass(generateAdapter = true)
data class ImagenPredictResponse(
    val predictions: List<ImagenPrediction>? = null
)

@JsonClass(generateAdapter = true)
data class ImagenPrediction(
    val bytesBase64Encoded: String? = null,
    val mimeType: String? = null
)

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = com.squareup.moshi.Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}
