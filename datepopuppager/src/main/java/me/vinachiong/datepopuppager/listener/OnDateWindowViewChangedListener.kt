package me.vinachiong.datepopuppager.listener

import me.vinachiong.datepopuppager.adapter.ItemDateModelRecyclerAdapter
import me.vinachiong.datepopuppager.model.DateModel

/**
 * 控件状态变更监听器
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal interface OnDateWindowViewChangedListener {
    /**
     * 按年，按月切换
     */
    fun onModeChanged(mode: Int)

    /**
     * 按月，切换显示年份
     */
    fun onMonthModeSwipeToYear(year: String)
}