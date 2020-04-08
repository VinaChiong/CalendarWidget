package me.vinachiong.datepopuppager

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.WindowManager
import android.widget.PopupWindow
import me.vinachiong.datepopuppager.adapter.ItemDateModelRecyclerAdapter
import me.vinachiong.datepopuppager.listener.OnItemDateModelCheckedChangedListener
import me.vinachiong.datepopuppager.model.DateModel

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class PopupWindowDialog(context: Context, manager: PagerAdapterManager) : PopupWindow(), OnItemDateModelCheckedChangedListener {

    init {
        manager.addOnItemDateModelCheckedChangedListeners(this)
        contentView = DateWindowView(context, manager)
        width = WindowManager.LayoutParams.MATCH_PARENT
        isFocusable = true
        isOutsideTouchable = false
        setBackgroundDrawable(ColorDrawable(0x00000000))
    }

    override fun onCheckChanged(dateModel: DateModel, position: Int, adapter: ItemDateModelRecyclerAdapter) {
        Log.d("PopupWindowDialog", "$dateModel")
        dismiss()
        // TODO 通知数据变更
    }
}