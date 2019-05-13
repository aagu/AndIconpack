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


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

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
        title.text = "Welcome to And Marshmallow"
        introduction.text = "这里是 Marshmallow, Material Design"
        iconNumber.text = "40"
        newNumber.text = "40"
        Glide.with(this).load(R.drawable.logo).into(img_star)

        ll_card_main3_rate.setOnClickListener {
            openAppStore(packageName = context!!.packageName)
        }
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
