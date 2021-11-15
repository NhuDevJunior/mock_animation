package com.example.animation_mock_nhuhc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.animation_mock_nhuhc.databinding.ItemSeekbarBinding

class AdapterSeekBar(private val listValue : MutableList<Int>): RecyclerView.Adapter<AdapterSeekBar.ViewHolder>() {

    inner class ViewHolder(private val binding : ItemSeekbarBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(value : Int){
            if((value - 100)%200 == 0){
                binding.itemSeekbar.scaleY = 0.7f
            }
            else {
                binding.itemSeekbar.scaleY = 0.2f
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemSeekbarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listValue[position])
    }

    override fun getItemCount(): Int {
        return listValue.size
    }
}