package me.vinachiong.datepopuppager.model

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
data class DateModel (
    var year: String = "",
    var month: String = "",
    var mode: Int = -1,
    var enable: Boolean = false,
    var checked: Boolean = false
) {
    var extraLabel: String = ""

    fun label(): String =
        when (mode){
            Mode.YEAR_MODE -> "${year}年"
            Mode.MONTH_MODE -> "${year}年${month}月"
            else -> extraLabel
        }

    fun isChecked(): Boolean = this.checked

    fun isEnabled(): Boolean = this.enable

    override fun equals(other: Any?): Boolean {
        if (null == other || other !is DateModel || other.mode != this.mode) return false

        if (other === this) return true

        return when (mode){
            Mode.YEAR_MODE -> other.year == this.year
            else -> other.year == this.year && other.month == this.month
        }
    }

    override fun hashCode(): Int {
        var result = year.hashCode()
        result = 31 * result + month.hashCode()
        result = 31 * result + mode
        result = 31 * result + enable.hashCode()
        result = 31 * result + checked.hashCode()
        return result
    }
}