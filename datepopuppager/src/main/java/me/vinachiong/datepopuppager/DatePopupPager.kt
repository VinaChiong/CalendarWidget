package me.vinachiong.datepopuppager

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.layout_date_popup_pager.view.*
import me.vinachiong.datepopuppager.adapter.CategoryPagerAdapter
import me.vinachiong.datepopuppager.model.DateModel

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
class DatePopupPager : RelativeLayout, PopupWindow.OnDismissListener {

    private lateinit var mContext: Activity
    // PopupWindow背景透明度动画
    private lateinit var windowAlphaAnimator: ValueAnimator
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

    private lateinit var viewPagerAdapter: CategoryPagerAdapter
    /**
     *  DateSticker 初始化
     */
    private fun initView() {
        mContext = context as Activity
        // 渲染layout布局
        LayoutInflater.from(context).inflate(R.layout.layout_date_popup_pager, this)

        // 初始化Animator
        windowAlphaAnimator = ValueAnimator.ofFloat(ORIGINAL_ALPHA, TARGET_ALPHA).apply {
            duration = 300
            addUpdateListener { animation: ValueAnimator ->
                mContext.window.attributes.alpha = animation.animatedValue as Float
            }
        }


        // 首次布局完成后，才进行组件初始化
        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            // 马上移除globalLayoutListener，避免重复初始化
            this.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            // 初始化ViewPager

            // 设置点击事件
            iv_expander.setOnClickListener {
                dispatchShow()
            }
        }
        this.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    fun initDateModel(startDate: String, endDate: String) {
        PagerAdapterManager.initAdapterDate(startDate, endDate, startDate)

        if (!::viewPagerAdapter.isInitialized) {
            viewPagerAdapter = CategoryPagerAdapter(PagerAdapterManager.categoryYearAdapterList,
                                                    PagerAdapterManager.categoryMonthAdapterList,
                                                    PagerAdapterManager.currentSelectData!!,
                                                    PagerAdapterManager.currentMode)
            viewPagerAdapter.onItemClickListener = object: CategoryPagerAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, data: DateModel) {
                    Toast.makeText(mContext, data.label(), Toast.LENGTH_LONG).show()
                    // TODO 通知外部
                }
            }
            vp_date_popup_pager.adapter = viewPagerAdapter
            vp_date_popup_pager.apply {
                val screenWidth = resources.displayMetrics.widthPixels
                offscreenPageLimit = 2
                pageMargin = screenWidth / 10 * 0
            }
        }
    }

    private lateinit var mPopupWindowDialog: PopupWindowDialog

    fun dispatchShow() {
        if (!::mPopupWindowDialog.isInitialized) {
            mPopupWindowDialog = PopupWindowDialog(mContext)
            val parentLocationArr = IntArray(2)
            this.getLocationOnScreen(parentLocationArr)
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val heightPixels: Int = screenHeight - parentLocationArr[1] - this.measuredHeight
            mPopupWindowDialog.height = heightPixels
        }
        mPopupWindowDialog.showAtLocation(this, Gravity.TOP, 0, 0)
        windowAlphaAnimator.reverse()
    }

    override fun onDismiss() {

    }

    fun dispatchDismiss() {
        if (::mPopupWindowDialog.isInitialized && mPopupWindowDialog.isShowing) {
            windowAlphaAnimator.start()
            mPopupWindowDialog.dismiss()
        }
    }

    companion object {
        /**  */
        private const val ORIGINAL_ALPHA = 1.0f
        /**  */
        private const val TARGET_ALPHA = 0.5f

        const val TYPE_YEAR = 0
        const val TYPE_MONTH = 1
    }
}