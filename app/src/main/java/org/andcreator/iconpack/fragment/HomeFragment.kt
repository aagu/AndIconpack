package org.andcreator.iconpack.fragment


import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

import org.andcreator.iconpack.R
import kotlinx.android.synthetic.main.fragment_home.*
import android.widget.Toast
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import org.andcreator.iconpack.activity.MainActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.net.URISyntaxException




/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : androidx.fragment.app.Fragment() {

    private var adaptations = 0

    companion object{
        private const val ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone"

        private const val INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&" +
                "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{payCode}%3F_s" +
                "%3Dweb-other&_t=1472443966571#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"

    }

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

        iconsPage.setOnClickListener {
            callbacks.callback(1)
        }

        newDialog.setOnClickListener {
            callbacks.callback(2)
        }

        openResource.setOnClickListener {
            startHttp(resources.getString(R.string.home_link))
        }

        ll_card_main3_rate.setOnClickListener {
            openAppStore(packageName = context!!.packageName)
        }

        donate.setOnClickListener {
            startIntentUrl(INTENT_URL_FORMAT.replace("{payCode}", resources.getString(R.string.alipay_code)))
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
        val xml = context!!.resources.getXml(R.xml.drawable)
        var type = xml.eventType
        try {
            while (type != XmlPullParser.END_DOCUMENT){
                when(type){
                    XmlPullParser.START_TAG ->{
                        if (xml.name == "item"){

                            adaptations++
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
            Toast.makeText(context, resources.getString(R.string.no_store), Toast.LENGTH_SHORT).show()
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


    private fun startIntentUrl(intentFullUrl: String): Boolean {

        if (hasInstalledAlipayClient(context!!)){
            try {
                val intent = Intent.parseUri(
                    intentFullUrl,
                    Intent.URI_INTENT_SCHEME
                )
                startActivity(intent)
                return true
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                return false
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                return false
            }
        }else{
            Toast.makeText(context, "未安装支付宝", Toast.LENGTH_SHORT).show()
        }

        return false
    }

    /**
     * 判断支付宝客户端是否已安装，建议调用转账前检查
     * @param context Context
     * @return 支付宝客户端是否已安装
     */
    private fun hasInstalledAlipayClient(context: Context): Boolean{
        val pm = context.packageManager
        try {
            val info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0)
            return info != null
        } catch (e: PackageManager.NameNotFoundException ) {
            e.printStackTrace()
            return false
        }
    }


}
