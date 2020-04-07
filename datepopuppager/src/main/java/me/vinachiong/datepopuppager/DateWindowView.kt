package me.vinachiong.datepopuppager

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_date_popup_pager.view.*
import kotlinx.android.synthetic.main.layout_date_window_pager.view.*
import kotlinx.android.synthetic.main.layout_date_window_pager.view.vp_date_popup_pager
import me.vinachiong.datepopuppager.adapter.CategoryPagerAdapter
import me.vinachiong.datepopuppager.adapter.DateWindowCategoryPagerAdapter
import me.vinachiong.datepopuppager.adapter.ItemDetailPagerAdapter
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
class DateWindowView: LinearLayout {

    private lateinit var mContext: Activity
    private lateinit var viewPagerAdapter: DateWindowCategoryPagerAdapter

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        mContext = context as Activity

        // 渲染layout布局
        LayoutInflater.from(context).inflate(R.layout.layout_date_window_pager, this)

        val first = PagerAdapterManager.categoryYearAdapterList.first()
        val last = PagerAdapterManager.categoryYearAdapterList.last()
        val yearLabel = DateModel()
        yearLabel.extraLabel = "${first.year}-${last.year}年"
        viewPagerAdapter = DateWindowCategoryPagerAdapter(yearLabel,
                                                PagerAdapterManager.categoryYearAdapterList,
                                                PagerAdapterManager.currentSelectData!!,
                                                PagerAdapterManager.currentMode)
        PagerAdapterManager.addOnViewStatusChangedListeners(viewPagerAdapter)
        vp_date_popup_pager.adapter = viewPagerAdapter
        vp_date_popup_pager.apply {
            val screenWidth = resources.displayMetrics.widthPixels
            offscreenPageLimit = 2
            pageMargin = screenWidth / 10 * 0
        }

        rg_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_for_year -> {
                    // 提取可用年列表
                    rb_for_year.setTextColor(Color.WHITE)
                    rb_for_month.setTextColor(Color.parseColor("#3399FF"))
                    iv_arrow_left.visibility = View.INVISIBLE
                    iv_arrow_right.visibility = View.INVISIBLE
                    PagerAdapterManager.dispatchOnModeChanged(Mode.YEAR_MODE)
                }
                R.id.rb_for_month -> {
                    // 提取可用年月份列表
                    rb_for_year.setTextColor(Color.parseColor("#3399FF"))
                    rb_for_month.setTextColor(Color.WHITE)
                    iv_arrow_left.visibility = View.VISIBLE
                    iv_arrow_right.visibility = View.VISIBLE
                    PagerAdapterManager.dispatchOnModeChanged(Mode.MONTH_MODE)
                }
            }
        }
        when (PagerAdapterManager.currentMode) {
            Mode.YEAR_MODE -> rb_for_year.isChecked = true
            Mode.MONTH_MODE -> rb_for_month.isChecked = true
        }

        val itemDetailPagerAdapter = ItemDetailPagerAdapter(PagerAdapterManager.categoryYearAdapterList,
                                                           PagerAdapterManager.popupPagerMonthData,
                                                           PagerAdapterManager.currentSelectData!!,
                                                           PagerAdapterManager.currentMode)
        PagerAdapterManager.addOnViewStatusChangedListeners(itemDetailPagerAdapter)
        vp_grid.addOnPageChangeListener(itemDetailPagerAdapter)
        vp_grid.adapter = itemDetailPagerAdapter

        // 设置左右按钮的切换
        iv_arrow_left.setOnClickListener {
            val targetPos = vp_grid.currentItem - 1
            if (targetPos in 0 until itemDetailPagerAdapter.count) {
                vp_grid.currentItem = targetPos
            }
        }

        iv_arrow_right.setOnClickListener {
            val targetPos = vp_grid.currentItem + 1
            if (targetPos in 0 until itemDetailPagerAdapter.count) {
                vp_grid.currentItem = targetPos
            }
        }
    }
}