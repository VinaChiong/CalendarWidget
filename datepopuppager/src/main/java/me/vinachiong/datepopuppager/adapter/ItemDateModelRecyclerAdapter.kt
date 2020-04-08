package me.vinachiong.datepopuppager.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import me.vinachiong.datepopuppager.PagerAdapterManager
import me.vinachiong.datepopuppager.R
import me.vinachiong.datepopuppager.listener.OnItemDateModelCheckedChangedListener
import me.vinachiong.datepopuppager.model.DateModel
import me.vinachiong.datepopuppager.model.Mode
import org.jetbrains.anko.dip

/**
 * 展示日期的RecyclerView Adapter
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
internal class ItemDateModelRecyclerAdapter(private val dateModelList: List<DateModel>, private val manager: PagerAdapterManager) : RecyclerView.Adapter<ItemDateModelRecyclerAdapter.VH>(), OnItemDateModelCheckedChangedListener {

    private var currentCheckedData: DateModel? = null
    private val mDataMode = dateModelList.first().type

    init {
        manager.addOnItemDateModelCheckedChangedListeners(this)
    }

    inner class VH(val view: RadioButton) : RecyclerView.ViewHolder(view) {
        init {
            val width = view.context.dip(65)
            val height = view.context.dip(30)
            val linearLayoutParam = LinearLayout.LayoutParams(width, height)
            linearLayoutParam.gravity = Gravity.CENTER
            view.also {

                it.layoutParams = RecyclerView.LayoutParams(linearLayoutParam)
                it.buttonDrawable = null
                it.setBackgroundResource(R.drawable.slt_date_sticker_popup_radio_btn)
                it.isChecked = false
                it.gravity = Gravity.CENTER
                it.setTextColor(Color.WHITE)
                it.textSize = 13f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(RadioButton(parent.context))
    }

    override fun getItemCount(): Int = dateModelList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = dateModelList[position]
        holder.view.also {
            it.setOnClickListener { view ->
                if (!data.checked) {
                    data.checked = !data.checked
                    notifyItemChanged(position)
                    manager.dispatchOnCheckChanged(data, position, this)
                }
            }

            it.text = when (data.type) {
                Mode.YEAR_MODE -> data.year
                else -> data.month
            }
            it.isEnabled = data.isEnabled()
            it.isChecked = data.isChecked()
            if (it.isEnabled && !it.isChecked) {
                it.setTextColor(Color.BLACK)
            } else {
                it.setTextColor(Color.WHITE)
            }

            if (null == currentCheckedData && data.isChecked()) {//
                currentCheckedData = data
            }
        }
    }

    fun setUnchecked(position: Int) {
        if (position in 0 until this.itemCount) {
            val data = dateModelList[position]
            if (data.isChecked()) {
                data.checked = false
                notifyItemChanged(position)
            }
        }
    }

    override fun onCheckChanged(dateModel: DateModel, position: Int, adapter: ItemDateModelRecyclerAdapter) {
        // 如果currentCheckedData is-not-null， 设置 checked = false
        currentCheckedData?.checked = false
        currentCheckedData = if (this == adapter && dateModelList.contains(dateModel)) {
            // 属于当前Adapter的dateModel实例，缓存到currentCheckedData，用于下次反选
            dateModel
        } else if (mDataMode == Mode.YEAR_MODE && dateModel.type == Mode.MONTH_MODE) {
            // 当前Adapter的是年份数据，且被点击的是月份数据
            // 可以自动确定选中
            val target = dateModelList.find { it.year == dateModel.year }
            target?.checked = true
            target
        } else {
            // 否则设置null
            null
        }
        notifyDataSetChanged()
    }
}