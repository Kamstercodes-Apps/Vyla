package com.kamstercodes.vyla.data.remote

import com.kamstercodes.vyla.data.remote.model.IconPackDto
import com.kamstercodes.vyla.data.remote.model.WallpaperDto
import com.kamstercodes.vyla.data.remote.model.WidgetDto
import retrofit2.http.GET
import retrofit2.http.Query

interface VylaApiService {
    @GET("icons")
    suspend fun getIconPacks(
        @Query("apiKey") apiKey: String,
        @Query("page") page: Int = 1
    ): List<IconPackDto>

    @GET("widgets")
    suspend fun getWidgets(
        @Query("apiKey") apiKey: String
    ): List<WidgetDto>

    @GET("wallpapers")
    suspend fun getWallpapers(
        @Query("apiKey") apiKey: String,
        @Query("category") category: String? = null
    ): List<WallpaperDto>

    companion object {
        const val BASE_URL = "https://api.vyla-styler.com/v1/"
    }
}
