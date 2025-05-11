package com.example.smart.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smart.R
import com.example.smart.databinding.RvFacilityInfoBinding
import com.example.smart.models.FacilityInfoModel

class FacilityInfoAdapter(
    private val clickedListener: (FacilityInfoModel) -> Unit
) : RecyclerView.Adapter<FacilityInfoAdapter.FacilityInfoAdapterViewModel>() {
    private val collections: ArrayList<FacilityInfoModel> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setItem(list: ArrayList<FacilityInfoModel>) {
        collections.clear()
        collections.addAll(list)
        this.notifyDataSetChanged()
    }

    inner class FacilityInfoAdapterViewModel(private val binding: RvFacilityInfoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(items: FacilityInfoModel) {
            binding.apply {
                tvStatus.text = items.issueStatus
                tvDateSubmitted.text = items.dateSubmitted
                textView38.text = items.issueName

                val context = root.context

                Glide.with(context)
                    .load(items.issueImageUri.toString())
                    .into(imageView12)

                when (items.issueStatus) {
                    "Pending" -> {
                        cvStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.pending_bg))
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.pending_main))
                    }
                    "Completed" -> {
                        cvStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green_bg))
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_main))
                    }
                    "Ongoing" -> {
                        cvStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.student_bg))
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.student_main))
                    }
                    else -> {
                        cvStatus.setCardBackgroundColor(ContextCompat.getColor(context, R.color.student_bg))
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.student_main))
                    }
                }

                root.setOnClickListener {
                    clickedListener(items)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityInfoAdapterViewModel {
        val binding: RvFacilityInfoBinding = RvFacilityInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FacilityInfoAdapterViewModel(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: FacilityInfoAdapterViewModel, position: Int) {
        holder.bind(collections[position])
    }

    // Display toast message
    private fun displayToastMessage(message: String, activity: Activity) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}