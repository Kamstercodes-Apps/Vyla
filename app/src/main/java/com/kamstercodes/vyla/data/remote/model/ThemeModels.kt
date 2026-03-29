package com.kamstercodes.vyla.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IconPackDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "iconUrl") val iconUrl: String,
    @Json(name = "author") val author: String
)

@JsonClass(generateAdapter = true)
data class WidgetDto(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String, // e.g., "clock", "weather"
    @Json(name = "previewUrl") val previewUrl: String
)

@JsonClass(generateAdapter = true)
data class WallpaperDto(
    @Json(name = "id") val id: String,
    @Json(name = "url") val url: String,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String,
    @Json(name = "category") val category: String
)
