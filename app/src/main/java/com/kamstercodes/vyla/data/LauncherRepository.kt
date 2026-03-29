package com.kamstercodes.vyla.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.LauncherApps
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Process
import android.util.Log
import com.kamstercodes.vyla.data.model.AppInfo
import com.kamstercodes.vyla.data.model.IconPackInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser

class LauncherRepository(private val context: Context) {

    private val TAG = "LauncherRepository"
    private val iconPackIntents = arrayOf(
        "com.novalauncher.THEME",
        "org.adw.launcher.THEMES.icons",
        "com.gau.go.launcherex.theme",
        "com.teslacoilsw.launcher.THEME"
    )

    private val iconPackCache = mutableMapOf<String, String>()
    private var currentLoadedPack: String? = null

    suspend fun getInstalledApps(iconPackPackageName: String? = null): List<AppInfo> = withContext(Dispatchers.IO) {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val user = Process.myUserHandle()
        val pm = context.packageManager
        
        if (iconPackPackageName != currentLoadedPack) {
            loadAppFilter(iconPackPackageName)
        }

        val iconPackRes = if (!iconPackPackageName.isNullOrEmpty() && iconPackPackageName != "none") {
            try { pm.getResourcesForApplication(iconPackPackageName) } catch (e: Exception) { null }
        } else null

        launcherApps.getActivityList(null, user).map { activityInfo ->
            val appPkg = activityInfo.applicationInfo.packageName
            val className = activityInfo.name
            // Standard format in appfilter: ComponentInfo{package/class}
            val componentKey = "ComponentInfo{$appPkg/$className}"
            
            val icon = if (iconPackRes != null && iconPackPackageName != null) {
                val drawableName = iconPackCache[componentKey]
                if (drawableName != null) {
                    val resId = iconPackRes.getIdentifier(drawableName, "drawable", iconPackPackageName)
                    if (resId != 0) {
                        try { iconPackRes.getDrawable(resId, null) } catch (e: Exception) { activityInfo.getIcon(0) }
                    } else {
                        activityInfo.getIcon(0)
                    }
                } else {
                    // Fallback to name-based matching if component not found
                    getIconFromPackFallback(iconPackRes, iconPackPackageName, appPkg) ?: activityInfo.getIcon(0)
                }
            } else {
                activityInfo.getIcon(0)
            }

            AppInfo(
                label = activityInfo.label.toString(),
                packageName = appPkg,
                icon = icon,
                category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activityInfo.applicationInfo.category
                } else {
                    -1
                }
            )
        }.sortedBy { it.label.lowercase() }
    }

    private fun loadAppFilter(packageName: String?) {
        iconPackCache.clear()
        currentLoadedPack = packageName
        if (packageName == null || packageName == "none") return

        try {
            val pm = context.packageManager
            val res = pm.getResourcesForApplication(packageName)
            val appFilterId = res.getIdentifier("appfilter", "xml", packageName)
            if (appFilterId != 0) {
                val xrp = res.getXml(appFilterId)
                var eventType = xrp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && xrp.name == "item") {
                        val componentValue = xrp.getAttributeValue(null, "component")
                        val drawableValue = xrp.getAttributeValue(null, "drawable")
                        if (componentValue != null && drawableValue != null) {
                            iconPackCache[componentValue] = drawableValue
                        }
                    }
                    eventType = xrp.next()
                }
                Log.d(TAG, "Loaded ${iconPackCache.size} icons from $packageName")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading appfilter for $packageName", e)
        }
    }

    suspend fun getAvailableIconPacks(): List<IconPackInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val iconPacks = mutableMapOf<String, IconPackInfo>()
        
        for (action in iconPackIntents) {
            val intent = Intent(action)
            val list = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            for (info in list) {
                val pkg = info.activityInfo.packageName
                if (!iconPacks.containsKey(pkg)) {
                    iconPacks[pkg] = IconPackInfo(
                        packageName = pkg,
                        label = info.loadLabel(pm).toString(),
                        icon = info.loadIcon(pm)
                    )
                }
            }
        }
        iconPacks.values.toList()
    }

    private fun getIconFromPackFallback(res: Resources, packPkg: String, appPkg: String): Drawable? {
        val resourceName = appPkg.replace(".", "_")
        val resId = res.getIdentifier(resourceName, "drawable", packPkg)
        return if (resId != 0) {
            try { res.getDrawable(resId, null) } catch (e: Exception) { null }
        } else null
    }
    
    fun launchApp(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
