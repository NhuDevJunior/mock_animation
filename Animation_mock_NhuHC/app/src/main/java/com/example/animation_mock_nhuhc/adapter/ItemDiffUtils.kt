package com.example.animation_mock_nhuhc.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.animation_mock_nhuhc.model.Item

class ItemDiffUtils(private val oldData: List<Item>, private val newData: List<Item>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldData[oldItemPosition]
        val newItem = newData[newItemPosition]

        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldData[oldItemPosition]
        val newItem = newData[newItemPosition]

        return oldItem == newItem
    }

}