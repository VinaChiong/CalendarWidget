package me.vinachiong.datepopuppager.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import me.vinachiong.datepopuppager.R
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode
import org.jetbrains.anko.dip

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class ItemDateModelRecyclerAdapter(val dateModelList: List<DateModel>): RecyclerView.Adapter<ItemDateModelRecyclerAdapter.VH>() {
    inner class VH(val view: RadioButton): RecyclerView.ViewHolder(view) {
        init {
            val width = view.context.dip(65)
            val height = view.context.dip(30)
            view.layoutParams = RecyclerView.LayoutParams(
                LinearLayout.LayoutParams(width, height).also {
                    it.gravity = Gravity.CENTER
                }
                )
            view.buttonDrawable = null
            view.setBackgroundResource(R.drawable.slt_date_sticker_popup_radio_btn)
            view.isChecked = false
            view.gravity = Gravity.CENTER
            view.setTextColor(Color.WHITE)
            view.textSize = 13f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(RadioButton(parent.context))
    }

    override fun getItemCount(): Int = dateModelList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = dateModelList[position]
        holder.view.also {
            it.text = when(data.type) {
                Mode.YEAR_MODE -> data.year
                else -> data.month
            }
            it.isEnabled = data.isEnabled()
            it.isChecked = data.isChecked()
        }
    }

}