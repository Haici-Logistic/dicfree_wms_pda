package com.dicfree.pda.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.dicfree.pda.R

class ComponentCommonDialog {
    companion object {
        fun show(
            context: Context,
            title: String = "",
            message: CharSequence,
            text1: String,
            text2: String = "",
            onClickListener1: ComponentDialogListener,
            onClickListener2: ComponentDialogListener = object : ComponentDialogListener {
                override fun onClick(dialog: Dialog, view: View) {
                    dialog.dismiss()
                }
            },
            gravity: Int = Gravity.CENTER,
            cancelable: Boolean = true,
            parseHtml: Boolean = false
        ): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(cancelable)
            dialog.setContentView(R.layout.dialog_common_component)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            val text = dialog.findViewById<TextView>(R.id.message)
            val titleView = dialog.findViewById<TextView>(R.id.title)
            text.gravity = gravity
            if(parseHtml){
                text.text= Html.fromHtml(message.toString(), Html.FROM_HTML_MODE_COMPACT)
            }else{
                text.text = message
            }
            text.post {
                val lineCount = text.lineCount
                text.gravity = if (lineCount > 2) Gravity.LEFT else Gravity.CENTER
            }
            if(title.isNullOrEmpty()){
                titleView.visibility=View.GONE
            }else{
                titleView.visibility=View.VISIBLE
                titleView.text= title
            }
            if (text2.isNullOrEmpty()) {
                dialog.findViewById<ViewGroup>(R.id.type_2).visibility = View.VISIBLE
                dialog.findViewById<ViewGroup>(R.id.type_1).visibility = View.GONE
                val text3Layout = dialog.findViewById<ViewGroup>(R.id.text3)
                val text3View = text3Layout.getChildAt(0) as TextView
                text3View.text = text1
                text3Layout.setOnClickListener { v -> onClickListener1.onClick(dialog, v) }
            } else {
                dialog.findViewById<ViewGroup>(R.id.type_2).visibility = View.GONE
                dialog.findViewById<ViewGroup>(R.id.type_1).visibility = View.VISIBLE
                val text1Layout = dialog.findViewById<ViewGroup>(R.id.text1)
                val text2Layout = dialog.findViewById<ViewGroup>(R.id.text2)
                val text1View = text1Layout.getChildAt(0) as TextView
                val text2View = text2Layout.getChildAt(0) as TextView
                text1View.text = text1
                text2View.text = text2
                text1Layout.setOnClickListener { v -> onClickListener1.onClick(dialog, v) }
                text2Layout.setOnClickListener { v -> onClickListener2.onClick(dialog, v) }
            }
            dialog.show()
            return dialog
        }
    }
}


interface ComponentDialogListener {
    fun onClick(dialog: Dialog, view: View)
}
