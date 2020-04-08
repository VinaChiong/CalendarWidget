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
internal class PopupWindowDialog(context: Context, manager: PagerAdapterManager) : PopupWindow() {

    init {
        contentView = DateWindowView(context, manager)
        width = WindowManager.LayoutParams.MATCH_PARENT
        isFocusable = true
        isOutsideTouchable = false
        setBackgroundDrawable(ColorDrawable(0x00000000))
    }
}