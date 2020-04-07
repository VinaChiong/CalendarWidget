package me.vinachiong.datepopuppager.model

import me.vinachiong.datepopuppager.ItemStatus

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal data class DateModel (
    var year: String = "",
    var month: String = "",
    var type: Int = -1,
    var enable: Boolean = false,
    var checked: Boolean = false
) : ItemStatus {

    fun label(): String =
        when (type){
            Mode.YEAR_MODE -> "${year}年"
            Mode.MONTH_MODE -> "${year}年${month}月"
            else ->""
        }

    override fun isChecked(): Boolean = this.checked

    override fun isEnabled(): Boolean = this.enable

    override fun equals(other: Any?): Boolean {
        if (null == other || other !is DateModel) return false

        if (other === this) return true

        return when (type){
            Mode.YEAR_MODE -> other.year == this.year
            else -> other.year == this.year && other.month == this.month
        }
    }
}