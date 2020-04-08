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
import me.vinachiong.datepopuppager.model.Mode

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class DateWindowView: LinearLayout {

    private lateinit var manager :PagerAdapterManager
    private lateinit var mContext: Activity
    private lateinit var viewPagerAdapter: DateWindowCategoryPagerAdapter

    constructor(context: Context?, manager: PagerAdapterManager) : super(context) {
        this.manager = manager
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    /**
     *
     * @param
     */
    fun setPagerAdapterManager(manager: PagerAdapterManager) {
        if (!this::manager.isInitialized) {
            this.manager = manager
            initView()
        }
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
        val itemDetailPagerAdapter = ItemDetailPagerAdapter(manager)
        manager.addOnDateWindowViewChangedListeners(itemDetailPagerAdapter)
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
        if (manager.canSwitchMode) {
            rg_group.visibility = View.VISIBLE
            rg_group.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb_for_year -> {
                        // 提取可用年列表
                        rb_for_year.setTextColor(Color.WHITE)
                        rb_for_month.setTextColor(Color.parseColor("#3399FF"))
                        // 按年，只有单页，不需要切换Pager
                        iv_arrow_left.visibility = View.INVISIBLE
                        iv_arrow_right.visibility = View.INVISIBLE
                        manager.dispatchOnModeChanged(Mode.YEAR_MODE) // dispatch event
                    }
                    R.id.rb_for_month -> {
                        // 提取可用年月份列表
                        rb_for_year.setTextColor(Color.parseColor("#3399FF"))
                        rb_for_month.setTextColor(Color.WHITE)
                        // 按月，可能多页，需要切换Pager
                        iv_arrow_left.visibility = View.VISIBLE
                        iv_arrow_right.visibility = View.VISIBLE
                        manager.dispatchOnModeChanged(Mode.MONTH_MODE) // dispatch event
                    }
                }
            }
            when (manager.currentMode) {
                Mode.YEAR_MODE -> rb_for_year.isChecked = true
                Mode.MONTH_MODE -> rb_for_month.isChecked = true
            }
        } else {
            rg_group.visibility = View.GONE
        }
    }

    /**
     * 初始化目录ViewPager
     */
    private fun initCategoryViewPager() {
        // 弹窗的目录ViewPager, 按月，显示可选的年份
        viewPagerAdapter = DateWindowCategoryPagerAdapter(manager)

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