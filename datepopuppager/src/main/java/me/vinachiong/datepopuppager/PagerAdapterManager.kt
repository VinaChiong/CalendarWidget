package me.vinachiong.datepopuppager

import me.vinachiong.datepopuppager.listener.OnDateSelectedChangedListener
import me.vinachiong.datepopuppager.listener.OnDateWindowViewChangedListener
import me.vinachiong.datepopuppager.listener.OnItemDateModelCheckedChangedListener
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode

/**
 * PagerAdapter相关内容的管理器
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class PagerAdapterManager {

    /** 仅接受yyyyMM */
    internal var startDate: String = ""
    /** 仅接受yyyyMM */
    internal var endDate: String = ""

    internal var currentMode: Int = Mode.MONTH_MODE
        private set
    internal var currentSelectData: DateModel? = null
    internal var canSwitchMode = true
        private set

    // 目录 年份列表
    internal val categoryYearAdapterList = mutableListOf<DateModel>()
    // 目录 月份列表
    internal val categoryMonthAdapterList = mutableListOf<DateModel>()

    internal val popupPagerMonthData = mutableMapOf<String, MutableList<DateModel>>()

    /**
     * DateWindowView UI变化监听
     */
    private val onDateWindowViewChangedListeners = mutableListOf<OnDateWindowViewChangedListener>()
    /**
     * 选中的DateModel发生变化监听
     */
    private val onItemDateModelCheckedChangedListeners = mutableListOf<OnItemDateModelCheckedChangedListener>()

    fun initAdapterDate(startDate: String, endDate: String, defaultDate: String, canSwitchMode: Boolean = true) {
        // 校验年份，只接受4位
        require(!(!startDate.isKjqj() || !endDate.isKjqj() || !defaultDate.isKjqj())) { "Not a kjqj value" }
        // 起始年份的大小关系校验
        require(
            startDate.toInt() <= endDate.toInt() && defaultDate.toInt() in startDate.toInt()..endDate.toInt()) { "begin year $startDate cannot greater than end year $endDate" }

        this.startDate = startDate
        this.endDate = endDate
//        this.currentYear = defaultDate.year()
//        this.currentMonth = defaultDate.month()
        this.canSwitchMode = canSwitchMode

        initCategoryYearPagerAdapter(startDate, endDate, defaultDate)
    }

    private fun initCategoryYearPagerAdapter(begin: String, end: String, defaultDate: String) {
        val currentYear = defaultDate.year()
        val currentMonth = defaultDate.month()
        val startYear = begin.year().toInt()
        val endYear = end.year().toInt()
        popupPagerMonthData.clear()
        (startYear..endYear).forEach { year ->
            val januaryOfYear = "${year}01" // 本年的一月
            val decemberOfYear = "${year}12" // 本年的十二月
            val first = (startDate.toInt()).coerceAtLeast(januaryOfYear.toInt()) // 计算本年的月份开区间边界
            val last = (endDate.toInt()).coerceAtMost(decemberOfYear.toInt())  // 计算本年的月份闭区间边界

            val yearData = DateModel("$year", "$last".month(), Mode.YEAR_MODE, true,  "$year" == currentYear)
            if (yearData.isChecked() && currentMode == Mode.YEAR_MODE) {
                currentSelectData = yearData
            }
            categoryYearAdapterList.add(yearData)

            val monthList = mutableListOf<DateModel>()
            (1..12).forEach { month ->
                val monthStr = String.format("%02d", month)
                val enable = "$year${monthStr}".toInt() in first..last
                val check = currentMonth.toInt() == month && currentYear.toInt() == year && currentMode == Mode.MONTH_MODE
                val data = DateModel("$year", monthStr, Mode.MONTH_MODE, enable, check)
                if (check) currentSelectData = data
                monthList.add(data)

                if (enable) categoryMonthAdapterList.add(data)
            }
            popupPagerMonthData[year.toString()] = monthList
        }
    }

    /**
     * 添加监听器
     * @param listener OnDateWindowViewChangedListener
     */
    fun addOnDateWindowViewChangedListeners(listener: OnDateWindowViewChangedListener) {
        if (!onDateWindowViewChangedListeners.contains(listener)) {
            onDateWindowViewChangedListeners.add(listener)
        }
    }

    /**
     * 添加监听器
     * @param listener OnItemDateModelCheckedChangedListener
     */
    fun addOnItemDateModelCheckedChangedListeners(listener: OnItemDateModelCheckedChangedListener) {
        if (!onItemDateModelCheckedChangedListeners.contains(listener)) {
            onItemDateModelCheckedChangedListeners.add(listener)
        }
    }

    private var mOnDateSelectedChangedListener: OnDateSelectedChangedListener? = null
    fun addOnDateSelectedChangedListener(listener: OnDateSelectedChangedListener) {
        mOnDateSelectedChangedListener = listener
    }

    /**
     * 移除所有监听器
     */
    fun removeAllListeners() {
        onDateWindowViewChangedListeners.clear()
    }

    /**
     * 触发'按月'、'按年'变更事件
     * @param mode [Mode.YEAR_MODE] 或者 [Mode.MONTH_MODE]
     */
    fun dispatchOnModeChanged(mode: Int) {
        if (mode == Mode.YEAR_MODE || mode == Mode.MONTH_MODE) {
            onDateWindowViewChangedListeners.forEach {
                it.onModeChanged(mode)
            }
        }
    }

    /**
     * 触发'按月'情况下，显示年份变更事件
     * @param year
     */
    fun dispatchOnMonthModeSwipeToYear(year: String) {
        onDateWindowViewChangedListeners.forEach {
            it.onMonthModeSwipeToYear(year)
        }
    }

    /**
     * 显示指定的date日期，为按月模式
     * @param date 'YYYYMM'
     */
    fun dispatchSwitchToMonthMode(date: String): DateModel? {
        return if (date.isKjqj()) {
            categoryMonthAdapterList.find {
                "${it.year}${it.month}" == date
            }?.also {
                dispatchOnCurrentDateModelChanged(it)
            }
        } else {
            null
        }
    }

    /**
     * 如果当前有选中的日期 & 可以显示按年，切换到按年模式显示
     */
    fun dispatchSwitchToYearMode(): DateModel? {
        return if (null != currentSelectData && canSwitchMode) {
            categoryYearAdapterList.find {
                it.year == currentSelectData!!.year
            }?.also {
                dispatchOnCurrentDateModelChanged(it)
            }
        } else {
            null
        }
    }

    fun dispatchOnCheckChanged(dateModel: DateModel) {
        onItemDateModelCheckedChangedListeners.forEach { it.onCheckChanged(dateModel) }
    }

    fun dispatchOnCurrentDateModelChanged(dateModel: DateModel) {
        if (currentSelectData !== dateModel) {
            currentSelectData?.checked = false
            dateModel.checked = true
            currentSelectData = dateModel
            currentMode = dateModel.mode
            mOnDateSelectedChangedListener?.onCurrentDateModelChanged(dateModel)
        }
    }

}
