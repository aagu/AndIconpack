package org.andcreator.iconpack.holder

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import org.andcreator.iconpack.R
import org.andcreator.iconpack.bean.LauncherItem
import org.andcreator.iconpack.util.Utils

class LauncherHolder(itemView: View, private var listener: OnLauncherClickListener) : RecyclerView.ViewHolder(itemView) {

    private lateinit var launcher: LauncherItem
    private val launcherLogo = itemView.findViewById<ImageView>(R.id.launcherLogo)
    private val launcherName = itemView.findViewById<TextView>(R.id.launcherName)

    init {
        itemView.setOnClickListener {
            this.listener.onLauncherClick(launcher)
        }
    }

    fun setItem(context: Context,item: LauncherItem){

        launcher = item

        val iconName = "ic_" + launcher.name.toLowerCase().replace(" ", "_")
        val iconResource = Utils.getIconResId(context.resources,context.packageName,iconName)

        val option = RequestOptions().priority(Priority.IMMEDIATE)
        Glide.with(context)
            .load(
                if (iconResource != 0)
                    iconResource
                else
                    Utils.getIconResId(
                        context
                            .resources, context.packageName, "ic_na_launcher"
                    )
            ).apply(option)
            .into(launcherLogo)
        launcherName.text = launcher.name

        if (isInstalled(context,launcher.isInstalled,launcher.packageName)){
            launcherLogo.colorFilter = null
            launcherName.setBackgroundColor(launcher.launcherColor)
            launcherName.setTextColor(context.resources.getColor(R.color.white))
        }else{
            launcherLogo.colorFilter = bnwFilter()
            launcherName.setBackgroundColor(Color.TRANSPARENT)
            launcherName.setTextColor(context.resources.getColor(R.color.text_color))
        }
    }

    private fun isInstalled(context: Context, isInstall: Int, packageName: String): Boolean {
        var endData = false
        if (isInstall == -1) {
            if ("org.cyanogenmod.theme.chooser" == packageName) {
                if (Utils.isAppInstalled(context, "org.cyanogenmod.theme.chooser") || Utils.isAppInstalled(
                        context,
                        "com.cyngn.theme.chooser"
                    )
                ) {
                    return true
                }
            } else {
                endData = Utils.isAppInstalled(context, packageName)
            }
        }
        // Caches this value, checking if a launcher is installed is intensive on processing
        return endData
    }

    private fun bnwFilter(): ColorFilter {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        return ColorMatrixColorFilter(matrix)
    }

    interface OnLauncherClickListener {
        fun onLauncherClick(item: LauncherItem)
    }

}