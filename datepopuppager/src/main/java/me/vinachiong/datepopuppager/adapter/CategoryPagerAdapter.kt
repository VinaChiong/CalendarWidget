package me.vinachiong.datepopuppager.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
internal class CategoryPagerAdapter(private val manager: PagerAdapterManager) : PagerAdapter(), ViewPager.OnPageChangeListener, OnDateWindowViewChangedListener {

    private var yearDataSource = mutableListOf<DateModel>()
    private var monthDataSource = mutableListOf<DateModel>()
    init {
        yearDataSource.addAll(manager.categoryYearAdapterList)
        monthDataSource.addAll(manager.categoryMonthAdapterList)

        manager.addOnDateWindowViewChangedListeners(this)
    }

    var onItemClickListener: OnItemClickListener? = null
    private lateinit var mHostView: ViewPager

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        mHostView = container as ViewPager
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

        val data = when (manager.currentMode) {
            Mode.YEAR_MODE -> yearDataSource[position]
            else -> monthDataSource[position]
        }
        textView.text = data.label()
        textView.setOnClickListener {
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
            when (manager.currentMode) {
                Mode.YEAR_MODE -> {
                    notifyDataSetChanged()
                }
                Mode.MONTH_MODE -> {
                    notifyDataSetChanged()
                }
            }
    }

    override fun getCount(): Int {
        return when (manager.currentMode) {
            Mode.YEAR_MODE -> yearDataSource.size
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

    }

    override fun onModeChanged(mode: Int) {
        switchMode(mode)
    }

    override fun onMonthModeSwipeToYear(year: String) {

    }
}