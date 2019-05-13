package org.andcreator.iconpack.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewPager
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.bumptech.glide.Glide
import org.andcreator.iconpack.R
import org.andcreator.iconpack.fragment.*
import org.andcreator.iconpack.listener.AppBarStateChangeListener
import org.andcreator.iconpack.util.ColorUtil
import org.andcreator.iconpack.util.DisplayUtil

import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: SectionsPagerAdapter
    private var fabStatus = 1
    private lateinit var requestsFragment: RequestFragment
    private val icons = ArrayList<Int>()

    /**
     * 获取未授权的权限
     */
    private lateinit var permissionList:MutableList<String>

    /**
     * 请求权限的返回值
     */
    private val permissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        getPermission()
        initView()

    }

    private fun initView(){
//        val wallpaperManager = WallpaperManager.getInstance(this)
//        Glide.with(this).load(wallpaperManager.drawable).into(headImg)

        loadIcons()
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this@MainActivity,R.color.white)) //设置还没收缩时状态下字体颜色
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this@MainActivity,R.color.black_text))//设置收缩后Toolbar上字体的颜色
        collapsingToolbar.title = resources.getString(R.string.app_name)
        collapsingToolbar.expandedTitleMarginBottom = DisplayUtil.dip2px(this,60f)

        adapter = SectionsPagerAdapter(supportFragmentManager)
        setupViewPager(pager)
        pager.offscreenPageLimit = 5
        tab.setupWithViewPager(pager)
        tab.setSelectedTabIndicatorColor(ContextCompat.getColor(this@MainActivity,R.color.white))

        appBar.addOnOffsetChangedListener(object : AppBarStateChangeListener(){
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?,i: Int,max: Int) {
                tab.tabTextColors = ColorStateList.valueOf(ColorUtil.getColor(ContextCompat.getColor(this@MainActivity,R.color.white),ContextCompat.getColor(this@MainActivity,R.color.text_color),i.toFloat(),max.toFloat()))
                tab.setSelectedTabIndicatorColor(ColorUtil.getColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimaryDark),ContextCompat.getColor(this@MainActivity,R.color.text_color),i.toFloat(),max.toFloat()))
            }
        })

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageSelected(p0: Int) {
                when(p0){
                    0 ->{
                        if (fabStatus!=1){
                            fabStatus = 1
                            changeFabIcon(R.drawable.ic_format_paint_white_24dp)
                        }
                    }
                    1 ->{
                        if (fabStatus!=1){
                            fabStatus = 1
                            changeFabIcon(R.drawable.ic_format_paint_white_24dp)
                        }
                    }

                    2 ->{
                        fabStatus = 2
                        changeFabIcon(R.drawable.ic_send_white_24dp)
                        requestsFragment = adapter.getFragment(2) as RequestFragment
                        requestsFragment.setCallbackListener(object : RequestFragment.Callbacks{
                            override fun callback(position: Int) {
                                if (position == 0){
                                    hide(fab)
                                }else{
                                    show(fab)
                                }
                            }
                        })
                    }
                    3 ->{
                        if (fabStatus!=1){
                            fabStatus = 1
                            changeFabIcon(R.drawable.ic_format_paint_white_24dp)
                        }
                    }
                    4 ->{
                        if (fabStatus!=1){
                            fabStatus = 1
                            changeFabIcon(R.drawable.ic_format_paint_white_24dp)
                        }
                    }
                }
            }

        })

        refresh.setOnClickListener {
            loadIcons()
        }


        fab.setOnClickListener {
            if (fabStatus==1){
                pager.currentItem = 3
            }else{
                if (permissionList.isEmpty()){

                    val s = requestsFragment.getMessage()

                    if (s.isNotEmpty()){

                        val file = File(externalCacheDir,"requests.txt")

                        var out: FileOutputStream? = null
                        try {
                            if (!file.exists()) {
                                val files = File(file.parent)
                                files.mkdirs()
                                file.createNewFile()
                            }

                            out = FileOutputStream(file,false)
                            out.write(s.toByteArray())

                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            try {
                                if (out == null){
                                    return@setOnClickListener
                                }
                                out.flush()
                                out.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
                            "$packageName.provider", file))
                        sendIntent.type = "text/plain"
                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(Intent.createChooser(sendIntent,"发送给.."))
                    }else{
                        Snackbar.make(fab,"没有选中任何应用程序",Snackbar.LENGTH_SHORT).show()
                    }

                }else{
                    Snackbar.make(fab,"请到设置授予存储空间权限",Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPermission(){
        val permission = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionList = mutableListOf()
        permissionList.clear()

        //获取未授权的权限
        for (permiss:String in permission){
            if (ContextCompat.checkSelfPermission(this@MainActivity, permiss) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permiss)
            }
        }

        if (!permissionList.isEmpty()){
            //请求权限方法
            val permissions = permissionList.toTypedArray()
            ActivityCompat.requestPermissions(this@MainActivity, permissions, permissionCode)
        }else{

            val wallpaperManager = WallpaperManager.getInstance(this)
            Glide.with(this).load(wallpaperManager.drawable).into(headImg)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            permissionCode ->{
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"请转到设置授予权限，否则无法正常使用", Toast.LENGTH_SHORT).show()
                }else{
                    permissionList.clear()
                    val wallpaperManager = WallpaperManager.getInstance(this)
                    Glide.with(this).load(wallpaperManager.drawable).into(headImg)
                }
            }
            else ->{}
        }
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    private fun setupViewPager(viewPager: ViewPager) {
        adapter.addFragment(HomeFragment(),"主页")
        adapter.addFragment(IconsFragment(),"图标")
        adapter.addFragment(RequestFragment(),"图标适配")
        adapter.addFragment(ApplyFragment(),"应用")
        adapter.addFragment(AboutFragment(),"关于")
        viewPager.adapter = adapter
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

        fun getFragment(position: Int): Fragment {
            return mFragmentList[position]
        }
    }


    /**
     * 显示的动画
     */
    private fun show(view: View) {
        view.animate().cancel()

        // If the view isn't visible currently, we'll animate it from a single pixel
        view.alpha = 0f
        view.scaleY = 0f
        view.scaleX = 0f

        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(LinearOutSlowInInterpolator())
            .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animation: Animator) {
                    view.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {

                }
            })
    }

    /**
     * 隐藏的动画
     */
    private fun hide(view: View) {
        view.animate().cancel()
        view.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(FastOutLinearInInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                private var mCancelled: Boolean = false

                override fun onAnimationStart(animation: Animator) {
                    view.visibility = View.VISIBLE
                    mCancelled = false
                }

                override fun onAnimationCancel(animation: Animator) {
                    mCancelled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!mCancelled) {
                        view.visibility = View.INVISIBLE
                    }
                }
            })
    }

    private fun changeFabIcon(icon: Int){
        val animator = ObjectAnimator.ofFloat(fab, "rotation", 0f, 360f)
        animator.duration = 360
        animator.interpolator = DecelerateInterpolator()
        animator.start()

        fab.setImageResource(icon)
    }

    private fun loadIcons(){
        val rs =  ArrayList<Int>()

        doAsync {
            if (icons.size == 0){
                val xml = resources.getXml(R.xml.drawable)
                var type = xml.eventType
                try {
                    while (type != XmlPullParser.END_DOCUMENT){
                        when(type){
                            XmlPullParser.START_TAG ->{
                                if (xml.name == "item"){
//                            Log.e("6666666",xml.getAttributeValue(0))
//                            Log.e("6666666",xml.getAttributeValue(1))

                                    val drawableString = xml.getAttributeValue(0)
                                    val drawableId = resources.getIdentifier(drawableString,"drawable",this@MainActivity.packageName)
                                    icons.add(drawableId)
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

            var b = true
            while (b){
                var bo = true
                val r = Random.nextInt(icons.size)%(icons.size+1)
                for (j: Int in rs){
                    if (j == r){
                        //如果在数组里找到这个随机数则跳出循环
                        if (rs.size >= 4){
                            b = false
                        }
                        bo = false
                        break
                    }
                }

                if (bo){
                    if (rs.size < 4){
                        rs.add(r)
                    }
                }

            }

            uiThread {
                iconAnimator(icon1)
                iconAnimator(icon2)
                iconAnimator(icon3)
                iconAnimator(icon4)
                Glide.with(this@MainActivity).load(icons[rs[0]]).into(icon1)
                Glide.with(this@MainActivity).load(icons[rs[1]]).into(icon2)
                Glide.with(this@MainActivity).load(icons[rs[2]]).into(icon3)
                Glide.with(this@MainActivity).load(icons[rs[3]]).into(icon4)

            }
        }

    }

    private fun iconAnimator(v: View){

        val animator1 = ObjectAnimator.ofFloat(v, "scaleX", 0f, 1f)
        animator1.duration = 600
        animator1.interpolator = DecelerateInterpolator()
        animator1.start()

        val animator2 = ObjectAnimator.ofFloat(v, "scaleY", 0f, 1f)
        animator2.duration = 600
        animator2.interpolator = DecelerateInterpolator()
        animator2.start()

    }
}
