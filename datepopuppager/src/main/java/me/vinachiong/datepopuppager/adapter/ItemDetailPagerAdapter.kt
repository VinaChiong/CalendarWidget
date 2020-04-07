package me.vinachiong.datepopuppager.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import me.vinachiong.datepopuppager.SpaceDecorateItem
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
    mode: Int = Mode.MONTH_MODE) : PagerAdapter(), ViewPager.OnPageChangeListener {

    private var mMode: Int = Mode.MONTH_MODE
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

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = RecyclerView(container.context)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        view.layoutManager = GridLayoutManager(container.context, 4)

        val dataSource:List<DateModel> = when (mMode) {
            Mode.YEAR_MODE ->  yearDataSource
            else -> monthDataSource[mSelectedDateModel.year]?: listOf()
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
            Mode.YEAR_MODE ->  yearDataSource.size
            else -> monthDataSource.map {entry -> entry.key == mSelectedDateModel.year }.size
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
    }
}