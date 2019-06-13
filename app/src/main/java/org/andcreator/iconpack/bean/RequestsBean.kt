package org.andcreator.iconpack.bean

import android.graphics.drawable.Drawable

data class RequestsBean(
    val icon: Drawable?,
    val name: String?,
    val pagName: String?,
    val activityName: String?,
    val notAdaptation: Int,
    val adaptation: Int,
    val type: Int)