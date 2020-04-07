package me.vinachiong.datepopuppager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *
 *
 * @author vina.chiong@gmail.com
 * @version v1.0.0
 */
class SpaceDecorateItem(val left: Int, val top: Int, val right: Int, val bottom: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = top
        outRect.left = left
        outRect.right = right
        outRect.bottom = bottom
    }
}