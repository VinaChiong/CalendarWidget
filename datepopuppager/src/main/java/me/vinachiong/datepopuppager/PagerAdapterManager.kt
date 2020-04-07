package me.vinachiong.datepopuppager

import android.util.SparseArray
import androidx.core.text.isDigitsOnly
import androidx.viewpager.widget.ViewPager
import me.vinachiong.datepopuppager.listener.OnDateSelectedChangedListener
import me.vinachiong.datepopuppager.listener.OnDateWindowViewChangedListener
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal object PagerAdapterManager: ViewPager.SimpleOnPageChangeListener() {

    /** 仅接受yyyyMM */
    internal var startDate: String = ""
    /** 仅接受yyyyMM */
    internal var endDate: String = ""

    internal var currentMonth: String = ""
    internal var currentYear: String = ""
    internal var currentMode: Int = Mode.MONTH_MODE
    internal var currentSelectData: DateModel? = null

    // 目录 年份列表
    internal val categoryYearAdapterList = mutableListOf<DateModel>()
    // 目录 月份列表
    internal val categoryMonthAdapterList = mutableListOf<DateModel>()

    internal val popupPagerMonthData = mutableMapOf<String, MutableList<DateModel>>()


    private val onViewStatusChangedListeners =  mutableListOf<OnDateWindowViewChangedListener>()
    private val onDateSelectedChangedListeners =  mutableListOf<OnDateSelectedChangedListener>()

    fun addOnViewStatusChangedListeners(listenerDateWindow: OnDateWindowViewChangedListener) {
        if (!onViewStatusChangedListeners.contains(listenerDateWindow)) {
            onViewStatusChangedListeners.add(listenerDateWindow)
        }
    }

    fun addOnDateSelectedChangedListener(listener: OnDateSelectedChangedListener) {
        if (!onDateSelectedChangedListeners.contains(listener)) {
            onDateSelectedChangedListeners.add(listener)
        }
    }

    fun removeOnViewStatusChangedListeners(listenerDateWindow: OnDateWindowViewChangedListener) {
        if (onViewStatusChangedListeners.contains(listenerDateWindow)) {
            onViewStatusChangedListeners.remove(listenerDateWindow)
        }
    }

    fun removeOnDateSelectedChangedListener(listener: OnDateSelectedChangedListener) {
        if (onDateSelectedChangedListeners.contains(listener)) {
            onDateSelectedChangedListeners.remove(listener)
        }
    }

    fun dispatchOnModeChanged(mode: Int) {
        onViewStatusChangedListeners.forEach {
            it.onModeChanged(mode)
        }
    }

    fun dispatchOnMonthModeSwipeToYear(year: String) {
        onViewStatusChangedListeners.forEach {
            it.onMonthModeSwipeToYear(year)
        }
    }

    fun initAdapterDate(startDate: String, endDate: String, defaultDate: String) {
        // 校验年份，只接受4位
        require(!(!startDate.isKjqj() || !endDate.isKjqj() || !defaultDate.isKjqj())) { "Not a kjqj value" }
        // 起始年份的大小关系校验
        require(
            startDate.toInt() <= endDate.toInt() && defaultDate.toInt() in startDate.toInt()..endDate.toInt()) { "begin year $startDate cannot greater than end year $endDate" }

        this.startDate = startDate
        this.endDate = endDate
        this.currentYear = defaultDate.year()
        this.currentMonth = defaultDate.month()

        initCategoryYearPagerAdapter(startDate, endDate, defaultDate)
    }

    private fun initCategoryYearPagerAdapter(begin: String, end:String, defaultDate: String) {

        val startYear = begin.year().toInt()
        val endYear = end.year().toInt()
        popupPagerMonthData.clear()
        (startYear..endYear).forEach { year ->
            val januaryOfYear = "${year}01" // 本年的一月
            val decemberOfYear = "${year}12" // 本年的十二月
            val first = (startDate.toInt()).coerceAtLeast(januaryOfYear.toInt()) // 计算本年的月份开区间边界
            val last = (endDate.toInt()).coerceAtMost(decemberOfYear.toInt())  // 计算本年的月份闭区间边界

            val isYearMatch = "$year" == currentYear && currentMode == Mode.YEAR_MODE

            val yearData = DateModel("$year", "$last".month(), Mode.YEAR_MODE, true, isYearMatch)
            if (isYearMatch) currentSelectData = yearData
            categoryYearAdapterList.add(yearData)

            val monthList = mutableListOf<DateModel>()
            (1..12).forEach { month ->
                val monthStr = String.format("%02d", month)
                val enable = "$year${monthStr}".toInt() in first..last
                val check = currentMonth.toInt() == month && currentYear.toInt() == year &&  currentMode == Mode.MONTH_MODE
                val data = DateModel("$year", monthStr, Mode.MONTH_MODE, enable, check)
                if (check) currentSelectData = data
                monthList.add(data)

                if (enable) categoryMonthAdapterList.add(data)
            }
            popupPagerMonthData.put(year.toString(), monthList)
        }
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
    }

    fun resetAdapters(mode: Int) {
    }
}

private fun String.isKjqj(): Boolean {
    return this.isNotEmpty() && this.length == 6 && this.isDigitsOnly()
}

private fun String.year(): String {
    require(this.isKjqj())
    return this.substring(0..3)
}

private fun String.month(): String {
    require(this.isKjqj())
    return this.substring(4..5)
}