package org.andcreator.iconpack

import android.app.Application
import android.content.Context
import org.andcreator.iconpack.util.CrashHandler

class IconPack : Application() {

    override fun onCreate() {
        super.onCreate()
        CrashHandler.init(applicationContext)
    }
}