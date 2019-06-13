package org.andcreator.iconpack.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.andcreator.iconpack.R
import org.andcreator.iconpack.activity.ImageDialog
import org.andcreator.iconpack.adapter.IconsAdapter
import org.andcreator.iconpack.bean.IconsBean
import kotlinx.android.synthetic.main.fragment_icons.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 *
 */
class IconsFragment : Fragment() {

    private val iconsList = ArrayList<IconsBean>()
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
        recyclerIcons.layoutManager = GridLayoutManager(context,4)
        adapter = IconsAdapter(context!!,iconsList)
        recyclerIcons.adapter = adapter

        adapter.setClickListener(object : IconsAdapter.OnItemClickListener{
            override fun onClick(icon: Int, name: String) {
                val intent = Intent(context!!, ImageDialog::class.java)
                intent.putExtra("icon",icon)
                intent.putExtra("name",name)
                startActivity(intent)
            }
        })

        doAsync {
            loadIcons()
            uiThread {
                if (loading.visibility == View.VISIBLE){
                    loading.visibility = View.GONE
                }
                adapter.notifyDataSetChanged()
            }
        }

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
}
