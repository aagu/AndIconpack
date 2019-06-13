package org.andcreator.iconpack.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

import org.andcreator.iconpack.R
import kotlinx.android.synthetic.main.fragment_home.*
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    private var adaptations = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        title.text = resources.getString(R.string.home_title)
        introduction.text = resources.getString(R.string.home_subtitle)

        newNumber.text = resources.getString(R.string.new_icons)
        Glide.with(this).load(R.drawable.logo).into(img_star)

        openResource.setOnClickListener {
            startHttp(resources.getString(R.string.home_link))
        }

        ll_card_main3_rate.setOnClickListener {
            openAppStore(packageName = context!!.packageName)
        }

        doAsync{
            parser()
            uiThread {
                iconNumber.text = adaptations.toString()
            }
        }
    }


    private fun parser(){
        adaptations = 0
        val xml = context!!.resources.getXml(R.xml.appfilter)
        var type = xml.eventType
        try {
            while (type != XmlPullParser.END_DOCUMENT){
                when(type){
                    XmlPullParser.START_TAG ->{
                        if (xml.name == "item"){

                            val pkgActivity = xml.getAttributeValue(0)
                            if (pkgActivity.indexOf("{")+1<pkgActivity.indexOf("/")){
                                adaptations++
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


    private fun startHttp(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        context!!.startActivity(intent)
    }


    private fun openAppStore(packageName: String) {
        try {
            val uri = Uri.parse("market://details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "没有可用的应用商店", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }
}
