package org.andcreator.iconpack.adapter

import android.content.Context
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import org.andcreator.iconpack.R
import org.andcreator.iconpack.bean.RequestsBean

class RequestsAdapter(private val context: Context,
                      private var dataList: ArrayList<RequestsBean>,
                      private var checkRead: ArrayList<Boolean>) : RecyclerView.Adapter<RequestsAdapter.RequestHolder>(){

    private var isSelect = false

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RequestHolder {

        return RequestHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_app_to_request, p0, false))
    }

    override fun getItemCount(): Int {

        return dataList.size
    }

    override fun onBindViewHolder(p0: RequestHolder, p1: Int) {
        val bean = dataList[p1]
        Glide.with(context).load(bean.icon).into(p0.imgIcon)
        p0.txtName.text = bean.name

        p0.chkSelected.isChecked = checkRead[p1]

        p0.requestCard.setOnClickListener {
            checkRead[p1] = !p0.chkSelected.isChecked
            p0.chkSelected.isChecked = !p0.chkSelected.isChecked
        }
    }

    fun selectAll(){
        if (!isSelect){
            for (i in 1..dataList.size){

                checkRead[i-1] = true
            }
            isSelect = true
            notifyDataSetChanged()

        }else{
            for (i in 1..dataList.size){

                checkRead[i-1] = false
            }
            isSelect = false
            notifyDataSetChanged()
        }
    }

    fun getSelect(): ArrayList<Boolean>{
        return checkRead
    }

    class RequestHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        var txtName: TextView = itemView.findViewById(R.id.txtName)
        var chkSelected: AppCompatCheckBox = itemView.findViewById(R.id.chkSelected)
        var requestCard:LinearLayout = itemView.findViewById(R.id.requestCard)

    }
}