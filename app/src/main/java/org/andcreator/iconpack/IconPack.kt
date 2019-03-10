package org.andcreator.iconpack

import android.app.Application
import org.andcreator.iconpack.util.CrashHandler

class IconPack : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.init(applicationContext)
    }
}