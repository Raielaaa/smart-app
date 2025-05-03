package com.example.smart.ui.report_issue

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smart.R
import com.example.smart.databinding.FragmentIssueInfoBinding

class IssueInfoFragment : Fragment() {
    private val viewModel: IssueInfoViewModel by viewModels()
    private lateinit var binding: FragmentIssueInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIssueInfoBinding.inflate(inflater, container, false)

        binding.apply {
            cvSubmitIssue.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.custom_dialog_confirmation_dialog, null)

                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setCancelable(false)
                    .create()

                // Show the dialog
                dialog.show()


                // setting transparent background to apply custom corners
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                // setting custom width (25dp margins on both sides)
                val metrics = resources.displayMetrics
                val marginInPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    metrics
                ).toInt()
                val screenWidth = metrics.widthPixels
                val dialogWidth = screenWidth - (marginInPx * 2)

                dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }

        return binding.root
    }
}