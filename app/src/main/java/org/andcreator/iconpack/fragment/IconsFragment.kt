package org.andcreator.iconpack.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import org.andcreator.iconpack.R
import org.andcreator.iconpack.activity.ImageDialog
import org.andcreator.iconpack.adapter.IconsAdapter
import org.andcreator.iconpack.bean.IconsBean
import kotlinx.android.synthetic.main.fragment_icons.*
import org.andcreator.iconpack.util.doAsyncTask
import org.andcreator.iconpack.util.onUI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 *
 */
class IconsFragment : BaseFragment() {

    private val iconsList = ArrayList<IconsBean>()
    private var searchIconsList = ArrayList<IconsBean>()
    private lateinit var adapter: IconsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_icons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        recyclerIcons.layoutManager = GridLayoutManager(context, 4)
        adapter = IconsAdapter(context!!,iconsList)
        recyclerIcons.adapter = adapter
        recyclerIcons.addOnScrollListener(object : HideScrollListener() {

        })
        adapter.setClickListener(object : IconsAdapter.OnItemClickListener{
            override fun onClick(icon: Int, name: String) {
                val intent = Intent(context!!, ImageDialog::class.java)
                intent.putExtra("icon",icon)
                intent.putExtra("name",name)
                startActivity(intent)
            }
        })
        reloadIcons()
    }

    private fun loadIcons(){
        iconsList.clear()
        val xml = context!!.resources.getXml(R.xml.drawable)
        var type = xml.eventType
        var category = "All"
        try {
            while (type != XmlPullParser.END_DOCUMENT){
                when(type){
                    XmlPullParser.START_TAG ->{

                        if (xml.name == "category" && xml.attributeCount == 1){
                            category = xml.getAttributeValue(0)
                        }

                        if (xml.name == "item"){
                            if (xml.attributeCount == 1){
                                val drawableString = xml.getAttributeValue(0)
                                val drawableId = context!!.resources.getIdentifier(drawableString,"drawable",context!!.packageName)
                                iconsList.add(IconsBean(category, drawableId,drawableString))
                            }else{
                                val drawableString = xml.getAttributeValue(0)
                                val drawableName = xml.getAttributeValue(1)
                                val drawableId = context!!.resources.getIdentifier(drawableString,"drawable",context!!.packageName)
                                iconsList.add(IconsBean(category, drawableId,drawableName))
                            }
                        }
                    }
                    XmlPullParser.TEXT ->{

                    }
                }
                type = xml.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun search(name: String) {
        if (name.isNotEmpty()){

            doAsyncTask {
                iconsList.clear()
                for (iconName in searchIconsList) {
                    if (iconName.name.contains(name, true)) {
                        iconsList.add(iconName)
                    }
                }
                if (isDestroyed){
                    onUI {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }else{
            reloadIcons()
        }
    }

    fun reloadIcons() {
        doAsyncTask {
            loadIcons()
            if (isDestroyed){
                onUI {
                    if (loading.visibility == View.VISIBLE){
                        loading.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                    searchIconsList.clear()
                    searchIconsList = iconsList.clone() as ArrayList<IconsBean>
                }
            }
        }
    }

    interface Callbacks {
        fun callback(position: Int)
    }

    private lateinit var callbacks: Callbacks

    fun setCallbackListener(callbacks: Callbacks) {
        this.callbacks = callbacks
    }

    private fun onHide() {
        callbacks.callback(0)
    }

    private fun onShow() {
        callbacks.callback(1)
    }

    //滑动监听
    internal open inner class HideScrollListener : RecyclerView.OnScrollListener() {
        private val HIDE_HEIGHT = 40
        private var scrolledInstance = 0
        private var toolbarVisible = true

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (toolbarVisible && dy > 0 || !toolbarVisible && dy < 0) {
                //recycler向上滚动时dy为正，向下滚动时dy为负数
                scrolledInstance += dy
            }
            if (scrolledInstance > HIDE_HEIGHT && toolbarVisible) {//当recycler向上滑动距离超过设置的默认值并且toolbar可见时，隐藏toolbar和fab
                onHide()
                scrolledInstance = 0
                toolbarVisible = false
            } else if (scrolledInstance < -HIDE_HEIGHT && !toolbarVisible) {//当recycler向下滑动距离超过设置的默认值并且toolbar不可见时，显示toolbar和fab
                onShow()
                scrolledInstance = 0
                toolbarVisible = true
            }
        }
    }

}
