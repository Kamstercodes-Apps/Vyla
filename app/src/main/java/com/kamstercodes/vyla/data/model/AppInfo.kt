package com.kamstercodes.vyla.data.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val category: Int = -1 // From ApplicationInfo.CATEGORY_*
)
