package com.example.animation_mock_nhuhc.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.animation_mock_nhuhc.ApplicationContext
import com.example.animation_mock_nhuhc.R
import com.example.animation_mock_nhuhc.databinding.ItemBinding
import com.example.animation_mock_nhuhc.model.HighlightItem
import com.example.animation_mock_nhuhc.model.Item
import com.example.animation_mock_nhuhc.uitls.Direction
import com.example.animation_mock_nhuhc.uitls.disable
import com.example.animation_mock_nhuhc.uitls.enableItem
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlin.reflect.KFunction1


class GalleryAdapter(private val itemContactSelected: ((Item) -> Unit)) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
    private val items = arrayListOf<Item>()
//    private var id = -1
    private val highlightItem = MutableStateFlow(HighlightItem(0, R.anim.left_to_right))
    private var highlightItemPos = 0
    inner class GalleryViewHolder(
        private val binding: ItemBinding,
        private val cb: ((Int) -> Unit),
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    cb.invoke(adapterPosition)
                }
            }


        }

        @DelicateCoroutinesApi
        @SuppressLint("SetTextI18n")
        fun bind(item: Item) {
            binding.avatar.setImageResource(item.path)
            binding.tvCash.text = "$ "+item.cash.toString()
            binding.tvName.text = item.name
            observerState()
        }

        @DelicateCoroutinesApi
        private fun observerState() {

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    highlightItem.collect{
                        highlightItem(it)
                    }
                }
            }
        }

        private fun highlightItem(highlightItem: HighlightItem) {
            if (layoutPosition == highlightItem.position) {
                binding.enableItem(highlightItem.translateX, items[layoutPosition])
            } else {
                if (binding.layoutDetailExpense.isVisible) {
                    binding.disable(items[layoutPosition])
                }
            }
        }
    }

    /**
     * update new data
     * @param newContacts
     */
    fun setData(newContacts: List<Item>) {
        try {
            val diffResult = DiffUtil.calculateDiff(
                ItemDiffUtils(
                    oldData = this.items,
                    newData = newContacts
                )
            )
            diffResult.dispatchUpdatesTo(this)
            this.items.clear()
            this.items.addAll(newContacts)
        } catch (ex: Exception) {
            this.items.clear()
            this.items.addAll(newContacts)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            this::onItemSelected
        )
    }



    @DelicateCoroutinesApi
    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(items[position])
//        holder.apply {
//            itemView.setOnClickListener{
//                id = position
//                notifyDataSetChanged()
//            }
//            if(id==position){
//                if(itemView.width==itemView.height) {
//                    val layoutParams = itemView.layoutParams
//                    layoutParams.width = (itemView.width * 2f).toInt()
//                    Log.i("NhuHC", "${itemView.width}")
//                    itemView.layoutParams = layoutParams
//                    val colorFrom: Int = ApplicationContext.getContext().resources.getColor(R.color.white)
//                    val colorTo: Int = ApplicationContext.getContext().resources.getColor(items[id].color)
//                    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
//                    colorAnimation.duration = 0 // milliseconds
//                    colorAnimation.addUpdateListener { animator -> itemView.setBackgroundColor(animator.animatedValue as Int) }
//                    colorAnimation.start()
//                }
//
//            }
//            else
//            {
//                if(itemView.width!=itemView.height){
//                    val layoutParams = itemView.layoutParams
//                    layoutParams.width = (itemView.width /2f).toInt()
//                    Log.i("NhuHC", "${itemView.width}")
//                    itemView.layoutParams = layoutParams
//                    itemView.setBackgroundColor(ContextCompat.getColor(ApplicationContext.getContext(), R.color.white))
//                }
//                itemView.setBackgroundColor(ContextCompat.getColor(ApplicationContext.getContext(), R.color.white))
//            }
//        }

    }

    private fun onItemSelected(position: Int) {
        itemContactSelected.invoke(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun changeHighlightItem(direction: Direction): Int {
        if (direction == Direction.LEFT) {
            if (highlightItemPos < items.size-1) {
                highlightItemPos++
                highlightItem.value = HighlightItem(
                    highlightItemPos,
                    R.anim.right_to_left
                )
            }
        } else {
            if (highlightItemPos > 0) {
                highlightItemPos--
                highlightItem.value = HighlightItem(
                    highlightItemPos,
                    R.anim.left_to_right
                )
            }
        }
//        budgetTextView.changeValue(currentList[highlightItemPos].amount)
        return highlightItemPos
    }

}