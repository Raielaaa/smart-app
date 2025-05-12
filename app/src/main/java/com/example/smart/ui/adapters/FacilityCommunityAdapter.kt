package com.example.smart.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smart.databinding.RvFacilityCommunityBinding
import com.example.smart.databinding.RvFacilityInfoBinding
import com.example.smart.models.FacilityCommunityModel

class FacilityCommunityAdapter(
    private val clickedListener: () -> Unit
) : RecyclerView.Adapter<FacilityCommunityAdapter.FacilityCommunityAdapterViewModel>() {
    private val collections: ArrayList<FacilityCommunityModel> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setItem(list: ArrayList<FacilityCommunityModel>) {
        collections.clear()
        collections.addAll(list)
        this.notifyDataSetChanged()
    }

    inner class FacilityCommunityAdapterViewModel(private val binding: RvFacilityCommunityBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(items: FacilityCommunityModel, clickedListener: () -> Unit) {
            binding.apply {
                tvFullName.text = items.communitySender
                tvContent.text = "${items.issueTitle}: ${items.communityContent}"
                tvDate.text = items.communityDate
                tvRole.text = items.communitySenderRole.toString().replaceFirstChar { it.uppercaseChar() }
                tvInitial.text = items.communitySender?.get(0).toString().uppercase()
            }
            binding.root.setOnClickListener {
                clickedListener()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityCommunityAdapterViewModel {
        val binding: RvFacilityCommunityBinding = RvFacilityCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FacilityCommunityAdapterViewModel(binding)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: FacilityCommunityAdapterViewModel, position: Int) {
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