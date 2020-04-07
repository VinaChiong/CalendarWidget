package me.vinachiong.datepopuppager

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_date_window_pager.view.*
import me.vinachiong.datepopuppager.adapter.ItemDetailPagerAdapter

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
class DateWindowView: LinearLayout {

    private lateinit var mContext: Activity

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

        rg_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_for_year -> {
                    // 提取可用年列表
                    rb_for_year.setTextColor(Color.WHITE)
                    rb_for_month.setTextColor(Color.parseColor("#3399FF"))
                    iv_arrow_left.visibility = View.INVISIBLE
                    iv_arrow_right.visibility = View.INVISIBLE
//                    DataConnection.currentMode = TYPE_YEAR
//                    // RadiaButton 切换 按年、按月，不需要保存数据，仅通过eventListener刷新UI
//                    eventListener.onViewStateChanged(DataConnection.currentYear, DataConnection.currentMonth,
//                                                     TYPE_YEAR)
                }
                R.id.rb_for_month -> {
                    // 提取可用年月份列表
                    rb_for_year.setTextColor(Color.parseColor("#3399FF"))
                    rb_for_month.setTextColor(Color.WHITE)
                    iv_arrow_left.visibility = View.VISIBLE
                    iv_arrow_right.visibility = View.VISIBLE
//                    DataConnection.currentMode = TYPE_MONTH
//                    // RadiaButton 切换 按年、按月，不需要保存数据，仅通过eventListener刷新UI
//                    eventListener.onViewStateChanged(DataConnection.currentYear, DataConnection.currentMonth,
//                                                     TYPE_MONTH)
                }
            }
        }

        val popupViewPagerAdapter = ItemDetailPagerAdapter(PagerAdapterManager.categoryYearAdapterList,
                                                           PagerAdapterManager.popupPagerMonthData,
                                                           PagerAdapterManager.currentSelectData!!,
                                                           PagerAdapterManager.currentMode)
        vp_grid.addOnPageChangeListener(popupViewPagerAdapter)
        vp_grid.adapter = popupViewPagerAdapter

        // 设置左右按钮的切换
        iv_arrow_left.setOnClickListener {
            val targetPos = vp_grid.currentItem - 1
            if (targetPos in 0 until popupViewPagerAdapter.count) {
                vp_grid.currentItem = targetPos
            }
        }

        iv_arrow_right.setOnClickListener {
            val targetPos = vp_grid.currentItem + 1
            if (targetPos in 0 until popupViewPagerAdapter.count) {
                vp_grid.currentItem = targetPos
            }
        }
    }
}