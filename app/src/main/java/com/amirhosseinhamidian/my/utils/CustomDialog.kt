package com.amirhosseinhamidian.my.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.amirhosseinhamidian.my.R


class CustomDialog(context: Context) {
    private val context: Context
    private var title: String? = null
    private var message: String? = null
    private val builder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private var positiveButtonText: String? = null
    private var negativeButtonText: String? = null
    private lateinit var posOnClick: (View) -> Unit
    private lateinit var negOnClick: (View) -> Unit
    private var cancelable = false
    private var positiveButtonColor = -1
    private var negativeButtonColor = -1

    init {
        this.context = context
        builder = AlertDialog.Builder(context)
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun setPositiveButton(
        positiveButtonText: String?,
        onClick: (View) -> Unit
    ) {
        this.positiveButtonText = positiveButtonText
        posOnClick = onClick
    }

    fun setNegativeButton(
        negativeButtonText: String?,
        onClick: (View) -> Unit
    ) {
        this.negativeButtonText = negativeButtonText
        negOnClick = onClick
    }

    @SuppressLint("MissingInflatedId")
    fun show() {
        val view: View = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val tvPosButton = view.findViewById<TextView>(R.id.tvPosButton)
        val tvNegButton = view.findViewById<TextView>(R.id.tvNegButton)
        if (negativeButtonColor != -1) tvNegButton.setTextColor(negativeButtonColor)
        if (positiveButtonColor != -1) tvPosButton.setTextColor(positiveButtonColor)
        if (title != null) tvTitle.text = title
        if (message != null) tvMessage.text = message
        if (positiveButtonText != null) {
            tvPosButton.text = positiveButtonText
            tvPosButton.setOnClickListener {
                posOnClick(it)
            }
        } else {
            tvPosButton.visibility = View.GONE
        }
        if (negativeButtonText != null) {
            tvNegButton.text = negativeButtonText
            tvNegButton.setOnClickListener{
                negOnClick(it)
            }
        } else {
            tvNegButton.visibility = View.GONE
        }
        builder.setView(view)
        alertDialog = builder.show()
        val cdDialogBackground = ColorDrawable(Color.TRANSPARENT)
        val idDrawable = InsetDrawable(cdDialogBackground, 24)
        alertDialog.window!!.setBackgroundDrawable(idDrawable)
    }

    fun dismiss() {
        alertDialog.dismiss()
    }

    interface OnPositiveButtonClickListener {
        fun onClick()
    }

    interface OnNegativeButtonClickListener {
        fun onClick()
    }

    fun setPositiveButtonColor(color: String?) {
        positiveButtonColor = Color.parseColor(color)
    }

    fun setPositiveButtonColor(color: Int) {
        positiveButtonColor = color
    }

    fun setNegativeButtonColor(color: String?) {
        negativeButtonColor = Color.parseColor(color)
    }

    fun setNegativeButtonColor(color: Int) {
        negativeButtonColor = color
    }

    fun setCancelable(cancelable: Boolean) {
        this.cancelable = cancelable
        builder.setCancelable(cancelable)
    }
}