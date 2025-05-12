package com.example.smart.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smart.databinding.RvFacilityHomeItemBinding
import com.example.smart.databinding.RvFacilityInfoBinding
import com.example.smart.models.FacilityHomeModel
import com.example.smart.models.FacilityInfoModel

class FacilityHomeAdapter(
    private val clickedListener: (FacilityHomeModel) -> Unit
) : RecyclerView.Adapter<FacilityHomeAdapter.FacilityHomeAdapterViewModel>() {
    private val collections: ArrayList<FacilityHomeModel> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setItem(list: ArrayList<FacilityHomeModel>) {
        collections.clear()
        collections.addAll(list)
        this.notifyDataSetChanged()
    }

    inner class FacilityHomeAdapterViewModel(private val binding: RvFacilityHomeItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(items: FacilityHomeModel) {
            binding.apply {
                tvRoomNumber.text = "Room number: ${items.roomNumber}"
                tvFloorNumber.text = "Floor number: ${items.roomNumber[0]}"
                tvIssueCount.text = items.reportCount
            }
            binding.root.setOnClickListener {
                clickedListener(items)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityHomeAdapterViewModel {
        val binding: RvFacilityHomeItemBinding = RvFacilityHomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FacilityHomeAdapterViewModel(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: FacilityHomeAdapterViewModel, position: Int) {
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