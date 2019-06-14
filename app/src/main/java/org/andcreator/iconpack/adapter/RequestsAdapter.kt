package org.andcreator.iconpack.adapter

import android.content.Context
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import org.andcreator.iconpack.R
import org.andcreator.iconpack.bean.RequestsBean
import org.andcreator.iconpack.view.FastScrollRecyclerView
import java.util.*
import kotlin.collections.ArrayList

class RequestsAdapter(private val context: Context,
                      private var dataList: ArrayList<RequestsBean>,
                      private var checkRead: ArrayList<Boolean>) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    override fun getSectionName(position: Int): String {
        return if (position > 0){
            dataList[position].name!!.substring(0, 1).toUpperCase(Locale.ENGLISH)
        }else{
            ""
        }
    }

    private var isSelect = false

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        when (p1){
            0 ->{
                return RequestHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_app_to_request, p0, false))
            }
            1 ->{
                return RequestHolderHead(LayoutInflater.from(p0.context).inflate(R.layout.header_requests, p0, false))
            }
        }

        return RequestHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_app_to_request, p0, false))
    }

    override fun getItemCount(): Int {

        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    override fun onBindViewHolder(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {

        val bean = dataList[p1]

        when (p0.itemViewType){
            0 ->{
                val holder: RequestHolder = p0 as RequestHolder
                Glide.with(context).load(bean.icon).into(holder.imgIcon)
                holder.txtName.text = bean.name

                if (p1 > 0){
                    holder.chkSelected.isChecked = checkRead[p1-1]
                }

                holder.requestCard.setOnClickListener {
                    checkRead[p1] = !p0.chkSelected.isChecked
                    holder.chkSelected.isChecked = !holder.chkSelected.isChecked
                }
            }
            1 ->{
                val holder: RequestHolderHead = p0 as RequestHolderHead

                holder.selectAll.setOnClickListener {
                    selectAll()
                }

                holder.notAdaptation.text = "未适配 "+bean.notAdaptation
                holder.adaptation.text = "适配 "+bean.adaptation
            }
        }
    }

    fun selectAll(){
        if (!isSelect){
            for (i in 0 until checkRead.size){

                checkRead[i] = true
            }
            isSelect = true
            notifyDataSetChanged()

        }else{
            for (i in 0 until checkRead.size){

                checkRead[i] = false
            }
            isSelect = false
            notifyDataSetChanged()
        }
    }

    fun getSelect(): ArrayList<Boolean>{
        return checkRead
    }

    class RequestHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){

        var imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        var txtName: TextView = itemView.findViewById(R.id.txtName)
        var chkSelected: AppCompatCheckBox = itemView.findViewById(R.id.chkSelected)
        var requestCard:LinearLayout = itemView.findViewById(R.id.requestCard)

    }

    class RequestHolderHead(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
        var adaptation: TextView = itemView.findViewById(R.id.adaptation)
        var notAdaptation: TextView = itemView.findViewById(R.id.notAdaptation)
        var selectAll: ImageButton = itemView.findViewById(R.id.selectAll)
    }
}