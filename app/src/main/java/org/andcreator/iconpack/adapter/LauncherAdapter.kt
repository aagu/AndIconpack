package org.andcreator.iconpack.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.andcreator.iconpack.bean.LauncherItem
import org.andcreator.iconpack.R
import org.andcreator.iconpack.holder.LauncherHolder

class LauncherAdapter(private val context: Context,
                      private val launchers: ArrayList<LauncherItem>,
                      private val listener: LauncherHolder.OnLauncherClickListener) : RecyclerView.Adapter<LauncherHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LauncherHolder {
        val inflater = LayoutInflater.from(p0.context)
        return LauncherHolder(inflater.inflate(R.layout.item_launcher, p0, false),listener)
    }

    override fun getItemCount(): Int {
        return launchers.size
    }

    override fun onBindViewHolder(p0: LauncherHolder, p1: Int) {
        p0.setItem(context, launchers[p0.adapterPosition])
    }
}