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
internal class DateWindowCategoryPagerAdapter(
    // 按年：数据源
    private val yearData: DateModel,
    monthData: List<DateModel>,
    selectModel: DateModel,
    mode: Int = Mode.MONTH_MODE
): PagerAdapter(), ViewPager.OnPageChangeListener, OnDateWindowViewChangedListener {

    // 按月：数据源
    private var sourceForMonthMode = mutableListOf<DateModel>().also {
        it.addAll(monthData)
    }
    // 当前选中的日期
    private var mSelectedDateModel: DateModel = selectModel
    // 当前的mode，控制Adapter的表现
    private var mMode: Int = mode
//    // 按月的选中位置
//    private var monthSelectedPosition: Int = 0
    // 点击监听
    var onItemClickListener: OnItemClickListener? = null

    private var responseToClick = true

    private lateinit var mHostView: ViewPager

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        mHostView = container as ViewPager
        mHostView.updateLayoutParams {
            this.width = mHostView.context.resources.displayMetrics.widthPixels / 3
        }
//        calculateSelectPosition()
    }

//    private fun calculateSelectPosition() {
//        monthSelectedPosition = sourceForMonthMode.indexOfFirst {
//            it == mSelectedDateModel
//        }
//    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val context = container.context
        val textView = TextView(context)
        textView.layoutParams = ViewPager.LayoutParams().also {
            it.width = ViewPager.LayoutParams.WRAP_CONTENT
            it.height = ViewPager.LayoutParams.WRAP_CONTENT
        }
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.BLACK)
        textView.textSize = 15f

        val data = when (mMode) {
            Mode.YEAR_MODE ->  yearData
            else -> sourceForMonthMode[position]
        }
        textView.text = data.label()
        textView.setOnClickListener {
            mHostView.currentItem = position
            onItemClickListener?.onItemClick(position, data)
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

    fun switchMode(mode: Int) {
        if (mMode != mode) {
            when (mode) {
                Mode.YEAR_MODE -> {
                    mMode = mode
                    notifyDataSetChanged()
                }
                Mode.MONTH_MODE -> {
                    mMode = mode
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getCount(): Int {
        return when (mMode) {
            Mode.YEAR_MODE ->  1
            else -> sourceForMonthMode.size
        }
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
            PagerAdapterManager.dispatchOnMonthModeSwipeToYear(sourceForMonthMode[position].year)
        }
    }

    override fun onModeChanged(mode: Int) {
        switchMode(mode)
    }

    override fun onCategoryDateChanged(dateModel: DateModel) {

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
}