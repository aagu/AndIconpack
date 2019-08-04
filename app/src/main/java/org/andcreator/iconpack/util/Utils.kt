package org.andcreator.iconpack.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import org.andcreator.iconpack.R

object Utils {

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        val installed: Boolean
        installed = try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        return installed
    }

    fun getIconResId(r: Resources, p: String, name: String): Int {
        val res = r.getIdentifier(name, "drawable", p)
        return if (res != 0) {
            res
        } else {
            0
        }
    }

    /**
     * 获取App版本号
     */
    fun getAppVersion(context: Context): Int{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode.toInt()
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        }
    }

    /**
     * 获取App版本号名称
     */
    fun getAppVersionName(context: Context): String{
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName

    }
}