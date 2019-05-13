package org.andcreator.iconpack.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.andcreator.iconpack.R
import org.andcreator.iconpack.adapter.AboutAdapter
import org.andcreator.iconpack.bean.AboutBean
import kotlinx.android.synthetic.main.fragment_about.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * A simple [Fragment] subclass.
 *
 */
class AboutFragment : Fragment() {

    private val credits = ArrayList<AboutBean>()
    private lateinit var adapter: AboutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        recyclerAbout.layoutManager = LinearLayoutManager(context!!)
        adapter = AboutAdapter(context!!,credits)
        recyclerAbout.adapter = adapter
        recyclerScroller.attachRecyclerView(recyclerAbout)

        doAsync {
            loadData()
            uiThread {
                if (loading.visibility == View.VISIBLE){
                    loading.visibility = View.GONE
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadData(){
        credits.clear()
        credits.add(AboutBean("And","图标包创作者",R.drawable.ic_and,R.drawable.logo_background, arrayListOf("Google+","Dribbble","Coolapk"), arrayListOf("http://bubbble.org","http://bubbble.org","http://bubbble.org")))
        credits.add(AboutBean("And","仪表盘开发者",R.drawable.ic_and,R.drawable.material_background, arrayListOf("Instagram","Dribbble","Coolapk"), arrayListOf("https://www.instagram.com/andrewmartin791/","http://www.coolapk.com/u/620606","http://www.coolapk.com/u/620606")))
    }
}
