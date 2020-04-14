package com.mlprogramming.anothertodolist.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mlprogramming.anothertodolist.R

class UiUtils {
    companion object {
        private val colorDrawableBackground = ColorDrawable(Color.parseColor("#f7f7f7"))

        fun getDeleteIcon(context: Context): Drawable {
            return ContextCompat.getDrawable(context, R.drawable.ic_remove)!!
        }

        fun getItemTouchHelper(
            deleteIcon: Drawable,
            touchHelperSwipe: TouchHelperSwipe
        ): ItemTouchHelper {
            val itemTouchHelperCallback =
                object :
                    ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        viewHolder2: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        swipeDirection: Int
                    ) {
                        touchHelperSwipe.onSwipe(viewHolder.adapterPosition)
                    }

                    override fun onChildDraw(
                        c: Canvas,
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        dX: Float,
                        dY: Float,
                        actionState: Int,
                        isCurrentlyActive: Boolean
                    ) {
                        val itemView = viewHolder.itemView
                        val iconMarginVertical =
                            (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                        if (dX > 0) {
                            colorDrawableBackground.setBounds(
                                itemView.left,
                                itemView.top,
                                dX.toInt(),
                                itemView.bottom
                            )
                            deleteIcon.setBounds(
                                itemView.left + iconMarginVertical,
                                itemView.top + iconMarginVertical,
                                itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                                itemView.bottom - iconMarginVertical
                            )
                        } else {
                            colorDrawableBackground.setBounds(
                                itemView.right + dX.toInt(),
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )
                            deleteIcon.setBounds(
                                itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                                itemView.top + iconMarginVertical,
                                itemView.right - iconMarginVertical,
                                itemView.bottom - iconMarginVertical
                            )
                            deleteIcon.level = 0
                        }

                        colorDrawableBackground.draw(c)

                        c.save()

                        if (dX > 0)
                            c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                        else
                            c.clipRect(
                                itemView.right + dX.toInt(),
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )

                        deleteIcon.draw(c)

                        c.restore()

                        super.onChildDraw(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    }
                }

            return ItemTouchHelper(itemTouchHelperCallback)
        }
    }
}

interface TouchHelperSwipe {
    fun onSwipe(position: Int)
}