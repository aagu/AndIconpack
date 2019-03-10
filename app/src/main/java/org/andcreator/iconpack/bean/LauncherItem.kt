package org.andcreator.iconpack.bean

data class LauncherItem( val name: String,
                         val packageName: String,
                         val launcherColor: Int,
                         val isInstalled: Int = -1)