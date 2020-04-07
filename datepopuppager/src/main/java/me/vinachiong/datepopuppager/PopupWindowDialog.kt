package me.vinachiong.datepopuppager

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.PopupWindow

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
class PopupWindowDialog(context: Context) : PopupWindow() {

    init {
        contentView = DateWindowView(context)
        width = WindowManager.LayoutParams.MATCH_PARENT
        isFocusable = true
        isOutsideTouchable = false
        setBackgroundDrawable(ColorDrawable(0x00000000))
    }
}