package me.vinachiong.datepopuppager.model

import me.vinachiong.datepopuppager.ItemStatus

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
data class DateModel (
    var year: String = "",
    var month: String = "",
    var type: Int = -1,
    var enable: Boolean = false,
    var checked: Boolean = false
) : ItemStatus {
    var extraLabel: String = ""

    fun label(): String =
        when (type){
            Mode.YEAR_MODE -> "${year}年"
            Mode.MONTH_MODE -> "${year}年${month}月"
            else ->extraLabel
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

    override fun hashCode(): Int {
        var result = year.hashCode()
        result = 31 * result + month.hashCode()
        result = 31 * result + type
        result = 31 * result + enable.hashCode()
        result = 31 * result + checked.hashCode()
        return result
    }
}