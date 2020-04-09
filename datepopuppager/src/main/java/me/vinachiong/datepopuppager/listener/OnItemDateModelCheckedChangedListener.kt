package me.vinachiong.datepopuppager.listener

import me.vinachiong.datepopuppager.model.DateModel

/**
 * 日期选择变更监听器
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal interface OnItemDateModelCheckedChangedListener {
    fun onCheckChanged(dateModel: DateModel)
}