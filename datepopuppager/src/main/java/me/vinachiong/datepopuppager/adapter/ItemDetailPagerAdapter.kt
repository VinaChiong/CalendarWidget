package me.vinachiong.datepopuppager.adapter

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.util.contains
import androidx.core.util.forEach
import androidx.core.view.updateLayoutParams
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import me.vinachiong.datepopuppager.PagerAdapterManager
import me.vinachiong.datepopuppager.SpaceDecorateItem
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
internal class ItemDetailPagerAdapter(
    yearData: List<DateModel>,
    monthGroupData: Map<String, List<DateModel>>,
    selectModel: DateModel,
    mode: Int = Mode.MONTH_MODE) : PagerAdapter(), ViewPager.OnPageChangeListener, OnDateWindowViewChangedListener {

    private var responseToClick = true
    private var mMode: Int = mode
    private var mSelectedDateModel: DateModel = selectModel
    private var yearDataSource = mutableListOf<DateModel>().also {
        it.addAll(yearData)
    }
    private var monthDataSource = mutableMapOf<String, List<DateModel>>().also {
        it.clear()
        monthGroupData.forEach { entry ->
            it[entry.key] = entry.value
        }
    }

    private lateinit var mHostView: ViewPager

    override fun startUpdate(container: ViewGroup) {
        super.startUpdate(container)
        mHostView = container as ViewPager
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = RecyclerView(container.context)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        view.layoutManager = GridLayoutManager(container.context, 4)

        val dataSource:List<DateModel> = when (mMode) {
            Mode.YEAR_MODE ->  yearDataSource
            else -> monthDataSource[yearDataSource[position].year]?: listOf()
        }

        view.adapter = ItemDateModelRecyclerAdapter(dataSource)
        view.addItemDecoration(SpaceDecorateItem(
            container.context.dip(2),
            container.context.dip(7),
            container.context.dip(2),
            container.context.dip(7)
        ))

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
        return when (mMode) {
            Mode.YEAR_MODE ->  1
            else -> yearDataSource.size
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (mMode == Mode.MONTH_MODE) {
            responseToClick = false
            PagerAdapterManager.dispatchOnMonthModeSwipeToYear(yearDataSource[position].year)
        }
    }

    override fun onModeChanged(mode: Int) {
        if (mMode != mode && (mode == Mode.MONTH_MODE || mode == Mode.YEAR_MODE)) {
            mMode = mode
            notifyDataSetChanged()
        }
    }

    override fun onCategoryDateChanged(dateModel: DateModel) {

    }

    override fun onMonthModeSwipeToYear(year: String) {
        if (!responseToClick) {
            responseToClick = true
        } else {
            if (mMode == Mode.MONTH_MODE) {
                val position = yearDataSource.indexOfFirst {
                    it.year == year
                }
                if (position > -1) {
                    mHostView.currentItem = position
                }
            }
        }
    }
}