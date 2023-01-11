package com.amirhosseinhamidian.my.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinhamidian.my.R

abstract class SwipeRecyclerviewItemCallback(context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val context = context
    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_outline_delete)
    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_outline_edit)
    private val intrinsicWidthDelete = deleteIcon!!.intrinsicWidth
    private val intrinsicHeightDelete = deleteIcon!!.intrinsicHeight
    private val intrinsicWidthEdit = editIcon!!.intrinsicWidth
    private val intrinsicHeightEdit = editIcon!!.intrinsicHeight
    private val bgDelete = GradientDrawable()
    private val bgEdit = GradientDrawable()
    private val bgColorDelete = ContextCompat.getColor(context, R.color.red)
    private val bgColorEdit = ContextCompat.getColor(context, R.color.blue)
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private var disablePositions: List<Int> = emptyList()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        disablePositions.forEach {
            if (viewHolder.adapterPosition == it) return 0
        }
        return super.getSwipeDirs(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                canvas,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(
                canvas,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
            return
        }

        // Draw the red delete background
        bgDelete.colors = intArrayOf(bgColorDelete , bgColorDelete)
        bgDelete.cornerRadius = context.dpToPx(16).toFloat()
        bgDelete.setBounds(
            itemView.right + dX.toInt() ,
            itemView.top,
            itemView.right ,
            itemView.bottom
        )

        bgDelete.draw(canvas)

        // Calculate position of delete icon
        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeightDelete) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeightDelete) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidthDelete
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeightDelete

        // Draw the delete icon
        deleteIcon!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        if(dX < -context.dpToPx(40)) deleteIcon.draw(canvas)

        // Draw the blue edit background
        bgEdit.colors = intArrayOf(bgColorEdit , bgColorEdit)
        bgEdit.cornerRadius = context.dpToPx(16).toFloat()
        bgEdit.setBounds(
            itemView.left ,
            itemView.top,
            itemView.left + dX.toInt() ,
            itemView.bottom
        )

        bgEdit.draw(canvas)

        // Calculate position of edit icon
        val editIconTop = itemView.top + (itemHeight - intrinsicHeightEdit) / 2
        val editIconMargin = (itemHeight - intrinsicHeightEdit) / 2
        val editIconLeft = itemView.left + editIconMargin
        val editIconRight = itemView.left + editIconMargin + intrinsicWidthEdit
        val editIconBottom = editIconTop + intrinsicHeightEdit

        // Draw the delete icon
        editIcon!!.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
        if(dX > context.dpToPx(40)) editIcon.draw(canvas)

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

    fun setDisablePositions(listPos : List<Int>) {
        disablePositions = listPos
    }
}