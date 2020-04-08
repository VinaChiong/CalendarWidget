package me.vinachiong.datepopuppager

import android.content.Context
import android.graphics.drawable.ColorDrawable
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
internal class PopupWindowDialog(context: Context, private val manager: PagerAdapterManager) : PopupWindow(), OnItemDateModelCheckedChangedListener {
    private val dateWindowView = DateWindowView(context, manager)
    init {
        manager.addOnItemDateModelCheckedChangedListeners(this)
        contentView = dateWindowView
        width = WindowManager.LayoutParams.MATCH_PARENT
        isFocusable = true
        isOutsideTouchable = false
        setBackgroundDrawable(ColorDrawable(0x00000000))
    }

    override fun onCheckChanged(dateModel: DateModel, position: Int, adapter: ItemDateModelRecyclerAdapter) {
        manager.dispatchOnCurrentDateModelChanged(dateModel)
        dismiss()
    }

    fun onShow() {
        dateWindowView.checkDataChanged()
    }

}