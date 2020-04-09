package me.vinachiong.datepopuppager.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import me.vinachiong.datepopuppager.PagerAdapterManager
import me.vinachiong.datepopuppager.listener.OnDateWindowViewChangedListener
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode

/**
 * 月份目录
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class DateWindowCategoryPagerAdapter(private val manager: PagerAdapterManager) : PagerAdapter(), ViewPager.OnPageChangeListener, OnDateWindowViewChangedListener {

    // 按年：数据源
    private val dataSourceForYearMode: List<DateModel>
    // 按月：数据源
    private var sourceForMonthMode = mutableListOf<DateModel>()
    private var mDataSource = mutableListOf<DateModel>()
    private var mMode = -1

    private var responseToClick = true
    private lateinit var mHostView: ViewPager
    var onItemClickListener: OnItemClickListener? = null

    init {
        // 弹窗的目录ViewPager, 按年，仅需要一个DateModel
        val first = manager.categoryYearAdapterList.first()
        val last = manager.categoryYearAdapterList.last()
        val yearLabel = DateModel()
        yearLabel.extraLabel = "${first.year}-${last.year}年"
        dataSourceForYearMode = listOf(yearLabel)
        sourceForMonthMode.addAll(manager.categoryYearAdapterList)
        switchDataSource(manager.currentMode)

        // 添加监听器，'按年'状态下，响应滑动切换年的事件
        manager.addOnDateWindowViewChangedListeners(this)
    }

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        mHostView = container as ViewPager
        mHostView.updateLayoutParams {
            this.width = mHostView.context.resources.displayMetrics.widthPixels / 3
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val textView = TextView(container.context).also {
            it.layoutParams = ViewPager.LayoutParams().also { lp ->
                lp.width = ViewPager.LayoutParams.WRAP_CONTENT
                lp.height = ViewPager.LayoutParams.WRAP_CONTENT
            }
            it.gravity = Gravity.CENTER
            it.setTextColor(Color.BLACK)
            it.textSize = 15f

            val data = mDataSource[position]
            it.text = data.label()
            it.setOnClickListener {
                mHostView.currentItem = position
                onItemClickListener?.onItemClick(position, data)
            }
        }

        container.addView(textView)
        return textView
    }

    override fun getItemPosition(obj: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return mDataSource.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, data: DateModel)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (mMode == Mode.MONTH_MODE) {
            responseToClick = false
            manager.dispatchOnMonthModeSwipeToYear(sourceForMonthMode[position].year)
        }
    }

    override fun onModeChanged(mode: Int) {
        switchDataSource(mode)
    }

    override fun onMonthModeSwipeToYear(year: String) {
        if (!responseToClick) {
            responseToClick = true
        } else {
            if (mMode == Mode.MONTH_MODE) {
                val position = sourceForMonthMode.indexOfFirst {
                    it.year == year
                }
                if (position > -1) {
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
                    mDataSource.clear()
                    mDataSource.addAll(dataSourceForYearMode)
                    notifyDataSetChanged()
                }
                Mode.MONTH_MODE -> {
                    mDataSource.clear()
                    mDataSource.addAll(sourceForMonthMode)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun showSelectedData() {
        if (::mHostView.isInitialized) {
            when (mMode) {
                Mode.MONTH_MODE -> {
                    // 切换到对应页面
                    manager.currentSelectData?.also { dateModel ->
                        // 仅匹配year
                        val position = sourceForMonthMode.indexOfFirst { it.year == dateModel.year }
                        if (position > -1) {
                            mHostView.currentItem = position
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