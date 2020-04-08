package me.vinachiong.datepopuppager

import androidx.core.text.isDigitsOnly

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */

internal fun String.isKjqj(): Boolean {
    return this.isNotEmpty() && this.length == 6 && this.isDigitsOnly()
}

internal fun String.year(): String {
    require(this.isKjqj())
    return this.substring(0..3)
}

internal fun String.month(): String {
    require(this.isKjqj())
    return this.substring(4..5)
}