package me.vinachiong.datepopuppager

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.layout_date_popup_pager.view.*
import me.vinachiong.datepopuppager.adapter.CategoryPagerAdapter
import me.vinachiong.datepopuppager.listener.OnDateSelectedChangedListener
import me.vinachiong.datepopuppager.model.DateModel

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
class DatePopupPager : RelativeLayout, PopupWindow.OnDismissListener {

    // PopupWindow背景透明度动画
    private val windowAlphaAnimator: ValueAnimator =  ValueAnimator.ofFloat(ORIGINAL_ALPHA, TARGET_ALPHA).apply {
        duration = 300
        addUpdateListener { animation: ValueAnimator ->
            mContext.window.attributes.alpha = animation.animatedValue as Float
        }
    }
    private val manager: PagerAdapterManager = PagerAdapterManager()
    private lateinit var categoryPagerAdapter: CategoryPagerAdapter
    private lateinit var mContext: Activity
    private lateinit var mPopupWindowDialog: PopupWindowDialog
    private lateinit var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }


    /**
     *  DateSticker 初始化
     */
    private fun initView() {
        mContext = context as Activity
        // 渲染layout布局
        LayoutInflater.from(context).inflate(R.layout.layout_date_popup_pager, this)

        // 首次布局完成后，才进行组件初始化
        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            // 马上移除globalLayoutListener，避免重复初始化
            this.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            // 初始化ViewPager

            // 设置点击事件
            iv_expander.setOnClickListener {
                dispatchShowDialog()
            }
        }
        this.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    fun initDateModel(startDate: String, endDate: String, defaultDate: String, canSwitchMode: Boolean) {
        manager.initAdapterDate(startDate, endDate, defaultDate, canSwitchMode)

        if (!::categoryPagerAdapter.isInitialized) {
            categoryPagerAdapter = CategoryPagerAdapter(manager)
            categoryPagerAdapter.onItemClickListener = object: CategoryPagerAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, data: DateModel) {
                    if (position != vp_date_popup_pager.currentItem) {
                        vp_date_popup_pager.currentItem = position
                    }
                }
            }
            vp_date_popup_pager.adapter = categoryPagerAdapter
            vp_date_popup_pager.addOnPageChangeListener(categoryPagerAdapter)
            vp_date_popup_pager.apply {
                val screenWidth = resources.displayMetrics.widthPixels
                updateLayoutParams<ViewGroup.LayoutParams> {
                    width = screenWidth / 3
                }
                offscreenPageLimit = 2
                pageMargin = screenWidth / 10 * 0
            }

            left_holder.setOnClickListener {
                val targetPos = vp_date_popup_pager.currentItem - 1
                if (targetPos in 0 until categoryPagerAdapter.count) {
                    vp_date_popup_pager.currentItem = targetPos
                }
            }
            right_holder.setOnClickListener {
                val targetPos = vp_date_popup_pager.currentItem + 1
                if (targetPos in 0 until categoryPagerAdapter.count) {
                    vp_date_popup_pager.currentItem = targetPos
                }
            }
        }
    }

    fun dispatchShowDialog() {
        if (!::mPopupWindowDialog.isInitialized) {
            mPopupWindowDialog = PopupWindowDialog(mContext, manager)
            mPopupWindowDialog.setOnDismissListener(this)
            val parentLocationArr = IntArray(2)
            this.getLocationOnScreen(parentLocationArr)
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val heightPixels: Int = screenHeight - parentLocationArr[1] - this.measuredHeight
            mPopupWindowDialog.height = heightPixels
        }
        mPopupWindowDialog.showAsDropDown(this, Gravity.TOP, -this.measuredHeight, 0)
        mPopupWindowDialog.onShow()
        windowAlphaAnimator.reverse()
    }

    override fun onDismiss() {
        windowAlphaAnimator.start()
        if (::categoryPagerAdapter.isInitialized) {
            categoryPagerAdapter.checkDataChanged()
        }
    }

    fun dispatchDismiss() {
        if (::mPopupWindowDialog.isInitialized && mPopupWindowDialog.isShowing) {
            windowAlphaAnimator.start()
            mPopupWindowDialog.dismiss()
        }
    }

    fun switchToYearMode() {
        manager.dispatchSwitchToYearMode()?.run {
            if (::categoryPagerAdapter.isInitialized) {
                categoryPagerAdapter.checkDataChanged()
            }
        }
    }

    fun switchToMonthMode(date: String) {
        manager.dispatchSwitchToMonthMode(date)?.run {
            if (::categoryPagerAdapter.isInitialized) {
                categoryPagerAdapter.checkDataChanged()
            }
        }
    }

    fun addOnDateSelectedChangedListener(listener: OnDateSelectedChangedListener) {
        manager.addOnDateSelectedChangedListener(listener)
    }

    fun addOnDateSelectedChangedListener(listener: (DateModel) -> Unit) {
        manager.addOnDateSelectedChangedListener(object: OnDateSelectedChangedListener {
            override fun onCurrentDateModelChanged(dateModel: DateModel) {
                listener.invoke(dateModel)
            }
        })
    }

    companion object {
        /**  */
        private const val ORIGINAL_ALPHA = 1.0f
        /**  */
        private const val TARGET_ALPHA = 0.5f
    }
}