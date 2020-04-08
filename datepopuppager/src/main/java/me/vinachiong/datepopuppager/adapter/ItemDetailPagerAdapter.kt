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
import me.vinachiong.datepopuppager.listener.OnDateSelectedChangedListener
import me.vinachiong.datepopuppager.listener.OnDateWindowViewChangedListener
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode
import org.jetbrains.anko.dip

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class ItemDetailPagerAdapter(private val manager: PagerAdapterManager) : PagerAdapter(), ViewPager.OnPageChangeListener, OnDateWindowViewChangedListener, OnDateSelectedChangedListener {

    private var responseToClick = true
    private var isFirstInit = true

    private var yearDataSource = mutableListOf<DateModel>()
    private var monthDataSource = manager.popupPagerMonthData

    //
    private val monthDataAdapters = SparseArray<ItemDateModelRecyclerAdapter>()
    private var yearDataAdapter: ItemDateModelRecyclerAdapter? = null
    private var lastCheckYearAdapterPosition = -1
    // 按月时候最后显示的页面index / last index of page that user swiped to in Mode.MONTH_MODE
    // Used to show last page of Mode.MONTH_MODE when switch from Mode.YEAR_MODE
    private var lastSwipeMonthPageItem = 0

    init {

        yearDataSource.addAll(manager.categoryYearAdapterList)
        monthDataSource.forEach { entry ->
            entry.value.forEachIndexed { index, dateModel ->
                if (manager.currentYear == dateModel.year && manager.currentMonth == dateModel.month) {
                    val yearIndex = yearDataSource.indexOfFirst { it.year == dateModel.year }
                    lastCheckYearAdapterPosition = yearIndex
                }
            }
        }
        manager.addOnDateWindowViewChangedListeners(this)
        manager.addOnDateSelectedChangedListener(this)
    }

    private lateinit var mHostView: ViewPager

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        mHostView = container as ViewPager
        if (isFirstInit) mHostView.currentItem = lastCheckYearAdapterPosition
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
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                      LinearLayout.LayoutParams.MATCH_PARENT)
        view.layoutManager = GridLayoutManager(container.context, 4)

        val adapter = when (manager.currentMode) {
            Mode.YEAR_MODE -> ItemDateModelRecyclerAdapter(yearDataSource, manager).also {
                yearDataAdapter = it
            }
            else -> {
                ItemDateModelRecyclerAdapter(monthDataSource[yearDataSource[position].year] ?: listOf(), manager).also {
                    monthDataAdapters.put(position, it)
                }
            }
        }

        view.adapter = adapter
        view.addItemDecoration(
            SpaceDecorateItem(container.context.dip(2), container.context.dip(7), container.context.dip(2),
                              container.context.dip(7)))

        container.addView(view)
        return view
    }

    override fun getItemPosition(obj: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return when (manager.currentMode) {
            Mode.YEAR_MODE -> 1
            else -> yearDataSource.size
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (monthDataAdapters.contains(position)) monthDataAdapters.remove(position)
        container.removeView(`object` as View)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (manager.currentMode == Mode.MONTH_MODE) {
            responseToClick = false
            manager.dispatchOnMonthModeSwipeToYear(yearDataSource[position].year)
        }
    }

    override fun onModeChanged(mode: Int) {
        notifyDataSetChanged()
        if (mode == Mode.MONTH_MODE) {
            mHostView.currentItem = lastSwipeMonthPageItem
        }
    }

    override fun onMonthModeSwipeToYear(year: String) {
        if (!responseToClick) {
            responseToClick = true
        } else {
            if (manager.currentMode == Mode.MONTH_MODE) {
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

    override fun onCurrentDateModelChanged(dateModel: DateModel) {
        // 响应数据变更
        when (dateModel.type) {
            Mode.YEAR_MODE -> {

            }

            Mode.MONTH_MODE -> {
                val yearPosition = yearDataSource.indexOfFirst { it == dateModel }
                if (yearPosition > -1) {
                    mHostView.currentItem = yearPosition
                }
                responseToClick = false
                manager.dispatchOnMonthModeSwipeToYear(dateModel.year)
            }
        }
    }
}