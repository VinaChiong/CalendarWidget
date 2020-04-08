package me.vinachiong.datepopuppager.adapter

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import me.vinachiong.datepopuppager.PagerAdapterManager
import me.vinachiong.datepopuppager.listener.OnDateSelectedChangedListener
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode

/**
 * 月份目录
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class CategoryPagerAdapter(private val manager: PagerAdapterManager) : PagerAdapter(), ViewPager.OnPageChangeListener, OnDateSelectedChangedListener {

    private var yearDataSource = mutableListOf<DateModel>()
    private var monthDataSource = mutableListOf<DateModel>()
    private var mMode = manager.currentMode
    init {
        yearDataSource.addAll(manager.categoryYearAdapterList)
        monthDataSource.addAll(manager.categoryMonthAdapterList)

        manager.addOnDateSelectedChangedListener(this)
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

        val data = when (mMode) {
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

    override fun getCount(): Int {
        return when (mMode) {
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

    private var responseToChangeEvent = true
    override fun onPageSelected(position: Int) {
        when (mMode) {
            Mode.YEAR_MODE -> {
                val dateModel = yearDataSource[position]
                manager.dispatchOnCurrentDateModelChanged(dateModel)
            }
            Mode.MONTH_MODE -> {
                val dateModel = monthDataSource[position]
                manager.dispatchOnCurrentDateModelChanged(dateModel)
            }
        }
    }

    override fun onCurrentDateModelChanged(dateModel: DateModel) {
        Log.d("CategoryPagerAdapter", "onCurrentDateModelChanged - $dateModel")
        if (!responseToChangeEvent) {
            responseToChangeEvent = true
        } else {
            if (mMode != dateModel.type) {
                mMode = dateModel.type
                notifyDataSetChanged()
            }

            when (mMode) {
                Mode.YEAR_MODE -> {
                    mHostView.currentItem = yearDataSource.indexOfFirst { it == dateModel }
                }
                Mode.MONTH_MODE -> {
                    mHostView.currentItem = monthDataSource.indexOfFirst { it == dateModel}
                }
            }
        }
    }
}