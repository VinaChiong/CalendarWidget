package me.vinachiong.datepopuppager.listener

import me.vinachiong.datepopuppager.model.DateModel

/**
 * 控件状态变更监听器
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal interface OnDateWindowViewChangedListener {

    fun onModeChanged(mode: Int)

    fun onCategoryDateChanged(dateModel: DateModel)

    fun onMonthModeSwipeToYear(year: String)
}