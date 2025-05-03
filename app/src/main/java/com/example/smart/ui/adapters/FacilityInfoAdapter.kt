package com.example.smart.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smart.databinding.RvFacilityInfoBinding
import com.example.smart.models.FacilityInfoModel

class FacilityInfoAdapter(
    private val clickedListener: () -> Unit
) : RecyclerView.Adapter<FacilityInfoAdapter.FacilityInfoAdapterViewModel>() {
    private val collections: ArrayList<FacilityInfoModel> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setItem(list: ArrayList<FacilityInfoModel>) {
        collections.clear()
        collections.addAll(list)
        this.notifyDataSetChanged()
    }

    inner class FacilityInfoAdapterViewModel(private val binding: RvFacilityInfoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(items: FacilityInfoModel, clickedListener: () -> Unit) {
            binding.apply {

            }
            binding.root.setOnClickListener {
                clickedListener()
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
        holder.bind(collections[position], clickedListener)
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