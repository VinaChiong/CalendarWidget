package me.vinachiong.datepopuppager.adapter

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.util.contains
import androidx.core.util.containsValue
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import me.vinachiong.datepopuppager.PagerAdapterManager
import me.vinachiong.datepopuppager.SpaceDecorateItem
import me.vinachiong.datepopuppager.listener.OnDateWindowViewChangedListener
import me.vinachiong.datepopuppager.listener.OnItemDateModelCheckedChangedListener
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode
import org.jetbrains.anko.dip

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class ItemDetailPagerAdapter(private val manager: PagerAdapterManager) : PagerAdapter(), ViewPager.OnPageChangeListener, OnDateWindowViewChangedListener, OnItemDateModelCheckedChangedListener {

    private var responseToClick = true

    private var yearDataSource = mutableListOf<DateModel>()
    private var monthDataSource = mutableMapOf<String, List<DateModel>>()
    private var mSelectedDateModel: DateModel = manager.currentSelectData!!

    // 注意： monthDataAdapters.size == yearDataSource.size
    private val monthDataAdapters = SparseArray<ItemDateModelRecyclerAdapter>()
    private var lastCheckMonthAdapter: ItemDateModelRecyclerAdapter? = null
    private var lastCheckMonthAdapterPosition = -1

    private var yearDataAdapter: ItemDateModelRecyclerAdapter? = null
    private var lastCheckYearAdapterPosition = -1
    // 按月时候最后显示的页面index / last index of page that user swiped to in Mode.MONTH_MODE
    // Used to show last page of Mode.MONTH_MODE when switch from Mode.YEAR_MODE
    private var lastSwipeMonthPageItem = 0
    init {
        yearDataSource.addAll(manager.categoryYearAdapterList)
        monthDataSource.also {
            it.clear()
            manager.popupPagerMonthData.forEach { entry ->
                it[entry.key] = entry.value
            }

            manager.addOnDateWindowViewChangedListeners(this)
        }
    }

    private lateinit var mHostView: ViewPager

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        mHostView = container as ViewPager
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = RecyclerView(container.context)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                      LinearLayout.LayoutParams.MATCH_PARENT)
        view.layoutManager = GridLayoutManager(container.context, 4)

        val dataSource: List<DateModel> = when (manager.currentMode) {
            Mode.YEAR_MODE -> yearDataSource
            else -> monthDataSource[yearDataSource[position].year] ?: listOf()
        }

        val adapter = ItemDateModelRecyclerAdapter(dataSource, this)
        when (manager.currentMode) {
            Mode.YEAR_MODE -> yearDataAdapter = adapter
            else -> monthDataAdapters.put(position, adapter)
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

    override fun onCheckChanged(dateModel: DateModel, position: Int, adapter: ItemDateModelRecyclerAdapter) {

        // Attention!!!
        // We can confirm which [year] item to be auto checked when user click [month] item in Mode.MONTH_MODE.
        // But not for [month] item while user click [year] item in Mode.YEAR_MODE
        when (manager.currentMode) {
            Mode.YEAR_MODE -> {
                // 反选选中的年item\
                yearDataAdapter?.setUnchecked(lastCheckYearAdapterPosition)
                // 当前选中的是年，无法确定要选中的月份
                // mark yearPosition
                lastCheckYearAdapterPosition = position
            }
            Mode.MONTH_MODE -> {
                // 反选选中的年item 和 月item
                lastCheckMonthAdapter?.setUnchecked(lastCheckMonthAdapterPosition)
                yearDataAdapter?.setUnchecked(lastCheckYearAdapterPosition)

                lastCheckMonthAdapter = adapter
                lastCheckMonthAdapterPosition = position

                // 当前选中的是月份，可以确定要选中年份
                // 因为monthDataAdapters.size == yearDataAdapter.size
                // 可以通过 yearDataSource index of dataMode.year , locate current checked yearPosition

                val yearPosition = yearDataSource.indexOfFirst {
                    it.year == dateModel.year
                }

                // set checked = true of data & notify change
                yearDataSource[yearPosition].checked = true
                yearDataAdapter?.notifyItemChanged(yearPosition)

                // mark yearPosition
                lastCheckYearAdapterPosition = yearPosition
            }
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
}