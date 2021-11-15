package com.example.animation_mock_nhuhc.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animation_mock_nhuhc.ApplicationContext
import com.example.animation_mock_nhuhc.R
import com.example.animation_mock_nhuhc.adapter.AdapterSeekBar
import com.example.animation_mock_nhuhc.uitls.DataFake
import com.example.animation_mock_nhuhc.adapter.GalleryAdapter
import com.example.animation_mock_nhuhc.databinding.FragmentExpenditureBinding
import com.example.animation_mock_nhuhc.model.Item
import com.example.animation_mock_nhuhc.ui.CenterLayoutManager
import com.example.animation_mock_nhuhc.ui.OffsetItemSeekBar
import com.example.animation_mock_nhuhc.uitls.Direction
import com.example.animation_mock_nhuhc.viewmodel.MyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ExpenditureFragment : Fragment() {
    private val myViewModel: MyViewModel by activityViewModels()
    private lateinit var binding: FragmentExpenditureBinding
    private var galleryAdapter: GalleryAdapter? = null
//    var firstCompletelyVisiblePosition = 0
//    var checkScroll = 0
    private var listItem: MutableList<Item> = ArrayList()
    private lateinit var adapterSeekBar: AdapterSeekBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExpenditureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initAction()
        listenerRcvScrolled()
        observerData()
    }

    private fun observerData() {
        myViewModel.observerPosHighLight.observe(viewLifecycleOwner,{
            val position = it
            val oldCost = binding.tvCost.text.toString().toInt()
            val newCost = listItem[position].cash
            val animator = ValueAnimator.ofInt(oldCost,newCost)
            animator.addUpdateListener{
                binding.tvCost.text = animator.animatedValue.toString()
            }
            animator.doOnEnd {
                binding.tvCost.setTextColor(ContextCompat.getColor(ApplicationContext.getContext(), R.color.black))
            }
            animator.doOnStart {
                binding.tvCost.setTextColor(ContextCompat.getColor(ApplicationContext.getContext(), R.color.black))
            }
            animator.duration = 1000
            animator.start()
            val posSeek = newCost/25
            binding.seekbar.scrollToPosition(posSeek.toInt())
        })
    }

    private fun getCurrentItem(): Int {
        return (binding.seekbar.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }
    private fun onCalculatorValueChanged(newPos : Int , oldPos : Int , oldValue: Int) : Int {
        if(newPos==0)
        {
            return 0
        }
        if(newPos == DataFake.getValue().size-1)
        {
            return DataFake.sum()
        }
        Log.i("NhuHC","$newPos")
        return oldValue+ 25*(newPos-oldPos)
    }
    private fun initAction() {
        binding.seekbar.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState === RecyclerView.SCROLL_STATE_IDLE) {
                    rollingNumber()
                }
            }

        })
        val pos = listItem[0].cash/25
        binding.seekbar.scrollToPosition(pos)
    }
    private fun rollingNumber(){
        val position = getCurrentItem()
        val oldCost = binding.tvCost.text.toString().toInt()
        val newCost = onCalculatorValueChanged(position,oldCost/25,oldCost)
        val animator = ValueAnimator.ofInt(oldCost,newCost)
        animator.addUpdateListener{
            binding.tvCost.text = animator.animatedValue.toString()
        }
        animator.doOnEnd {
            binding.tvCost.setTextColor(ContextCompat.getColor(ApplicationContext.getContext(), R.color.black))
        }
        animator.doOnStart {
            binding.tvCost.setTextColor(ContextCompat.getColor(ApplicationContext.getContext(), R.color.black))
        }
        animator.duration = 1000
        animator.start()
    }
    private fun initData() {
        listItem = DataFake.getDataFake()
        binding.tvCost.text = listItem[0].cash.toString()
            galleryAdapter?.setData(DataFake.getDataFake())
        binding.rvListExpenditure.clipToPadding = false // disabling clip to padding is critical
        lifecycleScope.launch {
            delay(20)
            val highlightItem =
                binding.rvListExpenditure.findViewHolderForAdapterPosition(0)?.itemView?.x
                    ?: 0
            binding.rvListExpenditure.smoothScrollBy(
                highlightItem.toInt() - binding.guideline2.x.toInt(),
                0
            )

        }
    }

    private fun initView() {
        // init recyclerview
        galleryAdapter = GalleryAdapter { item ->

        }
        val linearLayoutManager = CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvListExpenditure.apply {
            layoutManager =
                linearLayoutManager
            adapter = galleryAdapter
        }
        // init adapter seek bar
        val width = Resources.getSystem().displayMetrics.widthPixels
        adapterSeekBar = AdapterSeekBar(DataFake.getValue())
        val linearLayoutSeekBar = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.seekbar.adapter = adapterSeekBar
        binding.seekbar.layoutManager = linearLayoutSeekBar
        binding.seekbar.addItemDecoration(OffsetItemSeekBar(width,binding.guideline1.x))
        showView(binding.layoutSeekbar, R.anim.slide_up)

    }
    private fun showView(view: View, anim: Int) {
        val animation = AnimationUtils.loadAnimation(requireActivity(), anim)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(p0: Animation?) {
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })
        view.animation = animation
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun listenerRcvScrolled() {
        var currentOffsetX = 0
        var highlightPos = -1
        binding.rvListExpenditure.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentOffsetX += dx
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            }
        })
        binding.rvListExpenditure.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    currentOffsetX = 0
                }
                MotionEvent.ACTION_UP -> {
                    if (currentOffsetX > 0) {
                        highlightPos =
                            galleryAdapter?.changeHighlightItem(Direction.LEFT)
                                ?: 0
                    } else if (currentOffsetX != 0) {
                        highlightPos =
                            galleryAdapter?.changeHighlightItem(Direction.RIGHT)
                                ?: 0
                    }
                    currentOffsetX = 0
                    if (highlightPos != -1) {
                        lifecycleScope.launch {
                            delay(20)
                            val highlightItem =
                                binding.rvListExpenditure.findViewHolderForAdapterPosition(highlightPos)?.itemView?.x
                                    ?: 0
                            binding.rvListExpenditure.smoothScrollBy(
                                highlightItem.toInt() - binding.guideline2.x.toInt(),
                                0
                            )
                            myViewModel.setPostHighLight(highlightPos)
                        }
                    }
                }
            }
            false
        }
    }
}