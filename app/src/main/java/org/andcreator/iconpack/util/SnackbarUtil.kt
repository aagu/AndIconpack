package org.andcreator.iconpack.util

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import org.andcreator.iconpack.R


class SnackbarUtil {

    fun SnackbarUtil(context: Context, views: View, content: String) {

        val snackbar = Snackbar.make(views, content, Snackbar.LENGTH_SHORT)
        val view = snackbar.view
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        snackbar.show()

    }
}