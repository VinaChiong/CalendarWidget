package me.vinachiong.sample.datepopuppager

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 *
 *
 * @author changweiliang@kungeek.com
 * @version v3.6.0
 */
data class MenuInfo(
    val title: String,
    val subTitle: String = "",
    @DrawableRes val iconResId: Int = 0,
    @StringRes val urlResId: Int =0
)