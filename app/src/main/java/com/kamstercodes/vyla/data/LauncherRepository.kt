package com.kamstercodes.vyla.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.os.Process
import com.kamstercodes.vyla.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LauncherRepository(private val context: Context) {

    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val user = Process.myUserHandle()
        
        launcherApps.getActivityList(null, user).map { activityInfo ->
            val appInfo = activityInfo.applicationInfo
            AppInfo(
                label = activityInfo.label.toString(),
                packageName = appInfo.packageName,
                icon = activityInfo.getIcon(0),
                category = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    appInfo.category
                } else {
                    -1
                }
            )
        }.sortedBy { it.label.lowercase() }
    }
    
    fun launchApp(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
