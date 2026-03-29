package com.kamstercodes.vyla.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.LauncherApps
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Process
import com.kamstercodes.vyla.data.model.AppInfo
import com.kamstercodes.vyla.data.model.IconPackInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LauncherRepository(private val context: Context) {

    private val iconPackIntents = arrayOf(
        "com.novalauncher.THEME",
        "org.adw.launcher.THEMES.icons",
        "com.gau.go.launcherex.theme"
    )

    suspend fun getInstalledApps(iconPackPackageName: String? = null): List<AppInfo> = withContext(Dispatchers.IO) {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val user = Process.myUserHandle()
        val pm = context.packageManager
        
        val iconPackRes = if (!iconPackPackageName.isNullOrEmpty()) {
            try { pm.getResourcesForApplication(iconPackPackageName) } catch (e: Exception) { null }
        } else null

        launcherApps.getActivityList(null, user).map { activityInfo ->
            val appPkg = activityInfo.applicationInfo.packageName
            val label = activityInfo.label.toString()
            
            val icon = if (iconPackRes != null && iconPackPackageName != null) {
                getIconFromPack(iconPackRes, iconPackPackageName, appPkg) ?: activityInfo.getIcon(0)
            } else {
                activityInfo.getIcon(0)
            }

            AppInfo(
                label = label,
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

    suspend fun getAvailableIconPacks(): List<IconPackInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val iconPacks = mutableMapOf<String, IconPackInfo>()
        
        for (action in iconPackIntents) {
            val intent = Intent(action)
            val list = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
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

    private fun getIconFromPack(res: Resources, packPkg: String, appPkg: String): Drawable? {
        // Standard icon pack naming convention: icon_package_name (dots replaced with underscores)
        val resourceName = appPkg.replace(".", "_")
        val resId = res.getIdentifier(resourceName, "drawable", packPkg)
        return if (resId != 0) {
            try { res.getDrawable(resId, null) } catch (e: Exception) { null }
        } else {
            // Some packs use "app_package_name"
            val resIdAlt = res.getIdentifier("app_$resourceName", "drawable", packPkg)
            if (resIdAlt != 0) {
                try { res.getDrawable(resIdAlt, null) } catch (e: Exception) { null }
            } else null
        }
    }
    
    fun launchApp(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
