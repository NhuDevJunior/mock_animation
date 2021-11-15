package com.example.animation_mock_nhuhc.uitls

import com.example.animation_mock_nhuhc.R
import com.example.animation_mock_nhuhc.model.Item
import com.example.animation_mock_nhuhc.uitls.Constant.MAX_VALUE
import com.example.animation_mock_nhuhc.uitls.Constant.MIN_VALUE

object DataFake {
    private val listItem = arrayListOf<Item>(
        Item("Cafe",300, R.drawable.ic_cafe, R.color.green),
        Item("House",650, R.drawable.ic_house, R.color.blue),
        Item("Taxi",170, R.drawable.ic_taxi, R.color.red),
        Item("Gym",130, R.drawable.ic_gym, R.color.brown),
        Item("Love",150, R.drawable.ic_love, R.color.yellow),
        Item("Other",100, R.drawable.ic_other, R.color.purple_200)
    )
    fun getDataFake(): ArrayList<Item> {
        return listItem
    }
    fun getSizeList():Int{
        return 6
    }
    fun getValue(): MutableList<Int>{
        val list : MutableList<Int> = ArrayList()
        for(i in MIN_VALUE..MAX_VALUE step 50 ){
            list.add(i)
        }
        return list
    }
    fun sum():Int{
        return 1500
    }
}