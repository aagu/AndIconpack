package org.andcreator.iconpack.fragment


import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.andcreator.iconpack.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

import org.andcreator.iconpack.adapter.RequestsAdapter
import org.andcreator.iconpack.bean.RequestsBean
import kotlinx.android.synthetic.main.fragment_request.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 *
 */
class RequestFragment : Fragment() {

    /**
     * 未适配列表
     */
    private var appsList: ArrayList<RequestsBean> = ArrayList()
    /**
     * 选中列表
     */
    private var checked: ArrayList<Boolean> = ArrayList()
    /**
     * 已适配列表
     */
    private var adaptations: ArrayList<String> = ArrayList()
    private lateinit var adapter: RequestsAdapter
    private val message = StringBuilder()

    private var waysAdaptions = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        recyclerApps.layoutManager = LinearLayoutManager(context!!)
        adapter = RequestsAdapter(context!!,appsList,checked)

        recyclerApps.adapter = adapter

        recyclerApps.addOnScrollListener(object : HideScrollListener() {

        })

        doAsync{
            parser()
            loadData()
            uiThread{
                if (loading.visibility == View.VISIBLE){
                    loading.visibility = View.GONE
                }
                adapter.notifyDataSetChanged()

            }
        }

    }

    private fun loadData(){
        appsList.clear()
        message.clear()
        waysAdaptions = 0
        val pm = context!!.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN,null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(mainIntent,0)
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos, ResolveInfo.DisplayNameComparator(pm))
        for (reInfo: ResolveInfo in resolveInfos){
            var isHave = false
            val pkgName = reInfo.activityInfo.packageName // 获得应用程序的包名
            for (packageName: String in adaptations){
                if (packageName == pkgName){
                    waysAdaptions++
                    isHave = true
                    break
                }
            }
            if (isHave){
                continue
            }

            val activityName = reInfo.activityInfo.name // 获得该应用程序的启动Activity的name
            val appLabel = reInfo.loadLabel(pm) as String // 获得应用程序的Label
            val icon = reInfo.loadIcon(pm) // 获得应用程序图标
            // 为应用程序的启动Activity 准备Intent
            val launchIntent = Intent()
            launchIntent.component = ComponentName(
                pkgName,
                activityName
            )
            // 创建一个AppInfo对象，并赋值
            appsList.add(RequestsBean(icon,appLabel,pkgName,activityName,waysAdaptions,appsList.size,0)) // 添加至列表中

            checked.add(false)
        }

        appsList.add(0, RequestsBean(null,"",null,null,waysAdaptions,appsList.size,1))
        Log.e("不可能是：",waysAdaptions.toString())

    }

    private fun parser(){
        val xml = context!!.resources.getXml(R.xml.appfilter)
        var type = xml.eventType
        try {
            while (type != XmlPullParser.END_DOCUMENT){
                when(type){
                    XmlPullParser.START_TAG ->{
                        if (xml.name == "item"){

                            val pkgActivity = xml.getAttributeValue(0)
                            if (pkgActivity.indexOf("{")+1<pkgActivity.indexOf("/")){
                                val packageName = pkgActivity.substring(pkgActivity.indexOf("{")+1,pkgActivity.indexOf("/"))

                                adaptations.add(packageName)
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

    fun getMessage(): String{
        message.clear()
        for ((index,value) in adapter.getSelect().withIndex()){
            if (value){

                message.append("<!-- ${appsList[index].name} -->\r\n")
                message.append("<item component=\"ComponentInfo{${appsList[index].pagName}/${appsList[index].activityName}}\" drawable=\"${appsList[index].name?.toLowerCase()?.replace(" ", "_")}\" />")
                message.append("\r\n")
            }
        }
        return message.toString()
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
