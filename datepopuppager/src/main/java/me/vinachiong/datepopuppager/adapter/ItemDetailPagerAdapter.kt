package me.vinachiong.datepopuppager.adapter

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.util.contains
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import me.vinachiong.datepopuppager.PagerAdapterManager
import me.vinachiong.datepopuppager.SpaceDecorateItem
import me.vinachiong.datepopuppager.listener.OnDateWindowViewChangedListener
import me.vinachiong.datepopuppager.model.Mode
import org.jetbrains.anko.dip

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class ItemDetailPagerAdapter(private val manager: PagerAdapterManager) : PagerAdapter(),
    ViewPager.OnPageChangeListener, OnDateWindowViewChangedListener {
    // 按年、按月 数据源
    private val yearDataSource = manager.categoryYearAdapterList
    private val monthDataSource = manager.popupPagerMonthData

    private var dataSourceCount = 0
    private var mMode = -1
    private var currentYear: String = ""

    // 是否响应Swipe To Year事件。避免处理自身触发的事件
    private var responseToSwipeToYear = true
    // 是否第一次初始化
    private var isFirstInit = true
    private val monthDataAdapters = SparseArray<ItemDateModelRecyclerAdapter>()
    private var yearDataAdapter: ItemDateModelRecyclerAdapter? = null
    // 按月时候最后显示的页面index / last index of page that user swiped to in Mode.MONTH_MODE
    // Used to show last page of Mode.MONTH_MODE when switch from Mode.YEAR_MODE
    private var lastSwipeMonthPageItem = 0

    private lateinit var mHostView: ViewPager
    private lateinit var spaceDecorateItem: SpaceDecorateItem

    init {
        switchDataSource(manager.currentMode)
        currentYear = manager.currentSelectData?.year?:""
        manager.addOnDateWindowViewChangedListeners(this)
    }

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        mHostView = container as ViewPager
        if (!::spaceDecorateItem.isInitialized) {
            val dip2 = container.context.dip(2)
            val dip7 = container.context.dip(7)
            spaceDecorateItem = SpaceDecorateItem(dip2, dip7, dip2, dip7)
        }
        if (isFirstInit) {
            showSelectedData()
        }
    }

    override fun finishUpdate(container: ViewGroup) {
        super.finishUpdate(container)
        if (isFirstInit) {
            // 第一次初始化完毕，执行默认选中
            isFirstInit = false
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = RecyclerView(container.context)
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        view.layoutManager = GridLayoutManager(container.context, 4)

        val adapter = when (mMode) {
            Mode.YEAR_MODE -> ItemDateModelRecyclerAdapter(yearDataSource, manager).also {
                yearDataAdapter = it
            }
            else -> {
                ItemDateModelRecyclerAdapter(
                    monthDataSource[yearDataSource[position].year] ?: listOf(), manager
                ).also {
                    monthDataAdapters.put(position, it)
                }
            }
        }

        view.adapter = adapter
        view.addItemDecoration(spaceDecorateItem)

        container.addView(view)
        return view
    }

    override fun getItemPosition(obj: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int = dataSourceCount

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (monthDataAdapters.contains(position)) monthDataAdapters.remove(position)
        container.removeView(`object` as View)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (mMode == Mode.MONTH_MODE) {
            responseToSwipeToYear = false
            manager.dispatchOnMonthModeSwipeToYear(yearDataSource[position].year)
        }
    }

    override fun onModeChanged(mode: Int) {
        switchDataSource(mode)
        showSelectedData()
    }

    override fun onMonthModeSwipeToYear(year: String) {
        if (!responseToSwipeToYear) {
            responseToSwipeToYear = true
        } else {
            if (mMode == Mode.MONTH_MODE) {
                val position = yearDataSource.indexOfFirst {
                    it.year == year
                }
                if (position > -1) {
                    lastSwipeMonthPageItem = position
                    mHostView.currentItem = position
                }
            }
        }
    }

    private fun switchDataSource(mode: Int) {
        if (mMode != mode) {
            mMode = mode
            when (mode) {
                Mode.YEAR_MODE -> {
                    dataSourceCount = 1
                    notifyDataSetChanged()
                }
                Mode.MONTH_MODE -> {
                    dataSourceCount = yearDataSource.size
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun showSelectedData() {
        if (::mHostView.isInitialized) {
            when (mMode) {
                Mode.YEAR_MODE -> {
                    yearDataAdapter?.checkDataChanged()
                }

                Mode.MONTH_MODE -> {
                    manager.currentSelectData?.also { dateModel ->
                        // 切换到对应页面
                        val yearPosition = yearDataSource.indexOfFirst { it.year == dateModel.year }
                        if (yearPosition > -1) {
                            if (monthDataAdapters.contains(yearPosition)) {
                                monthDataAdapters[yearPosition].checkDataChanged()
                            }
                            mHostView.currentItem = yearPosition
                        }
                    }
                }
            }
        }
    }


    fun checkDataChanged() {
        switchDataSource(manager.currentMode)
        showSelectedData()
    }
}