package com.example.arrivalcharm.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.arrivalcharm.core.AppConst
import com.example.arrivalcharm.databinding.DialogInputBinding

class NameEditDialog(
    context: Context, private val title: String?,
    private val onConfirm: (String) -> Unit, private val onCancel: () -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogInputBinding

    init {
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogInputBinding.inflate(LayoutInflater.from(context))
            .apply { setContentView(this.root) }
        setLayout()
    }

    private fun setLayout() = with(binding) {
        val lp = FrameLayout.LayoutParams(
            AppConst.getScreenWidth(context),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rootLy.layoutParams = lp
        if (title.isNullOrEmpty()) {
            titleText.visibility = View.GONE
        } else {
            titleText.text = title
        }
        confirmBtn.setOnClickListener {
            dismiss()
            onConfirm.invoke(input.text.toString())
        }
        cancelBtn.setOnClickListener {
            dismiss()
            onCancel.invoke()
        }
    }
}