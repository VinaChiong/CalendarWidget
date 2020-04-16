package me.vinachiong.sample.datepopuppager

import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.youth.banner.transformer.DefaultTransformer
import kotlinx.android.synthetic.main.activity_main.*
import me.vinachiong.sample.datepopuppager.newpager.UltraViewPager
import org.jetbrains.anko.dip

class MainActivity : AppCompatActivity() {
    private var fraction: Float = 1f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val total = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val singleWidth = dip(135f).toFloat()
        fraction = singleWidth / total

        setSupportActionBar(toolbar)
        date_popup_pager.initDateModel("201605", "201905", "201801", true)
        date_popup_pager.addOnDateSelectedChangedListener {
            Snackbar.make(date_popup_pager, "选中${it.label()}", Snackbar.LENGTH_LONG).show()
        }

        view_pager.pageMargin = dip(10f)
        view_pager.setPageTransformer(false, DefaultTransformer())
        val adapter = MyAdapter(recommendMenuInfoList)

        view_pager.adapter = adapter
        view_pager.addOnPageChangeListener(adapter)
        view_pager.setCurrentItem(1)

        ultra_view_pager.also { ultraViewPager->
            ultraViewPager.setInfiniteLoop(true)
            ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)

            ultraViewPager.setHGap(dip(10f))
            ultraViewPager.setMultiScreen(fraction)
            ultraViewPager.setItemRatio(dip(135f).toDouble() / dip(90f).toDouble())
            ultraViewPager.viewPager.offscreenPageLimit = 2
        }

        ultra_view_pager.adapter = MyAdapter2(recommendMenuInfoList)


        auto_view_pager.setAdapter(MyAdapter2(recommendMenuInfoList) as PagerAdapter)
    }


    inner class MyAdapter2(private val menuList: List<MenuInfo>) : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int = menuList.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = layoutInflater.inflate(R.layout.rv_recommend_menu_item, container, false)
            val t = menuList[position]
            view.setBackgroundResource(t.iconResId)
            view.findViewById<TextView>(R.id.tv_title).text = t.title
            view.findViewById<TextView>(R.id.tv_sub_title).text = t.subTitle
            container.addView(view)
            return view
        }


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }


    inner class MyAdapter(menuList: List<MenuInfo>) : PagerAdapter(), ViewPager.OnPageChangeListener {
        private val data: List<MenuInfo>
        private val realCount: Int = menuList.size

        init {
            data = mutableListOf<MenuInfo>().also {
                it.add(0, menuList.last())
                it.addAll(menuList)
                it.add(menuList.first())
            }



        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int = data.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = layoutInflater.inflate(R.layout.rv_recommend_menu_item, container, false)
            val t = data[position]
            view.setBackgroundResource(t.iconResId)
            view.findViewById<TextView>(R.id.tv_title).text = t.title
            view.findViewById<TextView>(R.id.tv_sub_title).text = t.subTitle
            container.addView(view)
            return view
        }

        override fun getPageWidth(position: Int): Float {

            return fraction
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun onPageScrollStateChanged(state: Int) {
            val currentItem = view_pager.currentItem;
            when(state) {
                ViewPager.SCROLL_STATE_IDLE-> {
                    if (currentItem == 0) {
                        view_pager.setCurrentItem(realCount, false)
                    } else if (currentItem == realCount + 1) {
                        view_pager.setCurrentItem(1, false)
                    }
                }
                ViewPager.SCROLL_STATE_DRAGGING-> {
                    if (currentItem == realCount + 1) {
                        view_pager.setCurrentItem(1, false)
                    } else if (currentItem == 0) {
                        view_pager.setCurrentItem(realCount, false)
                    }
                }
                ViewPager.SCROLL_STATE_SETTLING-> {

                }
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
        }
    }

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
            R.id.action_switch_year -> {
                date_popup_pager.switchToYearMode()
                true
            }
            R.id.action_switch_month -> {
                date_popup_pager.switchToMonthMode("201801")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val recommendMenuInfoList = listOf(MenuInfo("公司注册", "工商注册+免费工商核名", R.drawable.bg_servers_purple),
                                               MenuInfo("公司审计", "精细核准企业经营及资产状况", R.drawable.bg_servers_blue),
                                               MenuInfo("个人税收筹划", "税收筹划方案量身打造", R.drawable.bg_servers_green),
                                               MenuInfo("税控器托管", "一站式解决开票、抄税、报税问题", R.drawable.bg_servers_red),
                                               MenuInfo("出口退税", "为您量身定制便捷的退税解决方案", R.drawable.bg_servers_orange))
}
