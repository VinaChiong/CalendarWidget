package me.vinachiong.datepopuppager.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode

/**
 * 月份目录
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class CategoryPagerAdapter(
    yearData: List<DateModel>,
    monthData: List<DateModel>,
    selectModel: DateModel,
    mode: Int = Mode.MONTH_MODE
): PagerAdapter(), ViewPager.OnPageChangeListener {
    private var yearDataSource = mutableListOf<DateModel>().also {
        it.addAll(yearData)
    }
    private var monthDataSource = mutableListOf<DateModel>().also {
        it.addAll(monthData)
    }
    private var mSelectedDateModel: DateModel = selectModel
    private var mMode: Int = mode

    private lateinit var mHostView: ViewPager
    private var yearSelectedPosition: Int = 0
    private var monthSelectedPosition: Int = 0
    var onItemClickListener: OnItemClickListener? = null

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        mHostView = container as ViewPager
        calculateSelectPosition()
    }

    private fun calculateSelectPosition() {
        yearSelectedPosition = yearDataSource.indexOfFirst {
            it == mSelectedDateModel
        }
        monthSelectedPosition = monthDataSource.indexOfFirst {
            it == mSelectedDateModel
        }
    }

    override fun finishUpdate(container: ViewGroup) {
        super.finishUpdate(container)
        when (mMode) {
            Mode.YEAR_MODE ->  mHostView.currentItem = yearSelectedPosition
            else -> mHostView.currentItem = monthSelectedPosition
        }
    }

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
            Mode.YEAR_MODE ->  yearDataSource[position]
            else -> monthDataSource[position]
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

    override fun getPageWidth(position: Int): Float {
        return 0.33f
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
            Mode.YEAR_MODE ->  yearDataSource.size
            else -> monthDataSource.size
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
        when (mMode) {
            Mode.YEAR_MODE ->   yearSelectedPosition = position
            else -> monthSelectedPosition  = position
        }
    }
}