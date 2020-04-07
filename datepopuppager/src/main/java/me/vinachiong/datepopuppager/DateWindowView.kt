package me.vinachiong.datepopuppager

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_date_window_pager.view.*
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

        initCategoryViewPager()
        initRadioGroupModeCheck()
        initItemDetailViewPager()
    }

    /**
     * 初始化弹窗的详情ViewPager
     */
    private fun initItemDetailViewPager() {
        val itemDetailPagerAdapter = ItemDetailPagerAdapter(
            PagerAdapterManager.categoryYearAdapterList,
            PagerAdapterManager.popupPagerMonthData,
            PagerAdapterManager.currentSelectData!!,
            PagerAdapterManager.currentMode
        )
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

    /**
     * 初始化弹窗的Mode切换控件
     */
    private fun initRadioGroupModeCheck() {
        rg_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_for_year -> {
                    // 提取可用年列表
                    rb_for_year.setTextColor(Color.WHITE)
                    rb_for_month.setTextColor(Color.parseColor("#3399FF"))
                    // 按年，只有单页，不需要切换Pager
                    iv_arrow_left.visibility = View.INVISIBLE
                    iv_arrow_right.visibility = View.INVISIBLE
                    PagerAdapterManager.dispatchOnModeChanged(Mode.YEAR_MODE) // dispatch event
                }
                R.id.rb_for_month -> {
                    // 提取可用年月份列表
                    rb_for_year.setTextColor(Color.parseColor("#3399FF"))
                    rb_for_month.setTextColor(Color.WHITE)
                    // 按月，可能多页，需要切换Pager
                    iv_arrow_left.visibility = View.VISIBLE
                    iv_arrow_right.visibility = View.VISIBLE
                    PagerAdapterManager.dispatchOnModeChanged(Mode.MONTH_MODE) // dispatch event
                }
            }
        }
        when (PagerAdapterManager.currentMode) {
            Mode.YEAR_MODE -> rb_for_year.isChecked = true
            Mode.MONTH_MODE -> rb_for_month.isChecked = true
        }
    }

    /**
     * 初始化目录ViewPager
     */
    private fun initCategoryViewPager() {
        // 弹窗的目录ViewPager, 按年，仅需要一个DateModel
        val first = PagerAdapterManager.categoryYearAdapterList.first()
        val last = PagerAdapterManager.categoryYearAdapterList.last()
        val yearLabel = DateModel()
        yearLabel.extraLabel = "${first.year}-${last.year}年"

        // 弹窗的目录ViewPager, 按月，显示可选的年份
        viewPagerAdapter = DateWindowCategoryPagerAdapter(
            yearLabel,
            PagerAdapterManager.categoryYearAdapterList,
            PagerAdapterManager.currentSelectData!!,
            PagerAdapterManager.currentMode
        )
        // 添加监听器，'按年'状态下，响应滑动切换年的事件
        PagerAdapterManager.addOnViewStatusChangedListeners(viewPagerAdapter)

        // 初始化ViewPager
        vp_date_popup_pager.adapter = viewPagerAdapter
        vp_date_popup_pager.addOnPageChangeListener(viewPagerAdapter)
        vp_date_popup_pager.apply {
            val screenWidth = resources.displayMetrics.widthPixels
            offscreenPageLimit = 2
            pageMargin = screenWidth / 10 * 0
        }

        left_holder.setOnClickListener {
            val targetPos = vp_date_popup_pager.currentItem - 1
            if (targetPos in 0 until viewPagerAdapter.count) {
                vp_date_popup_pager.currentItem = targetPos
            }
        }
        right_holder.setOnClickListener {
            val targetPos = vp_date_popup_pager.currentItem + 1
            if (targetPos in 0 until viewPagerAdapter.count) {
                vp_date_popup_pager.currentItem = targetPos
            }
        }
    }
}