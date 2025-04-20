package com.example.smart.utils

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.smart.R
import com.example.smart.databinding.FragmentLoadingDialogBinding

class LoadingDialogFragment(
    private val dialogTitle: String,
    private val dialogBody: String
) : DialogFragment() {
    private lateinit var binding: FragmentLoadingDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoadingDialogBinding.inflate(inflater, container, false)

        binding.apply {
            tvDialogTitle.text = dialogTitle
            tvDialogBody.text = dialogBody
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val metrics = resources.displayMetrics
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            25f,
            metrics
        ).toInt()
        val screenWidth = metrics.widthPixels
        val dialogWidth = screenWidth - (marginInPx * 2)

        dialog?.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}