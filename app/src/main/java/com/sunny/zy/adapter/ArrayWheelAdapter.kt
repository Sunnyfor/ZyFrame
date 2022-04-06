package com.sunny.zy.adapter

import com.contrarywind.adapter.WheelAdapter


/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2022/3/1 18:49
 */
class ArrayWheelAdapter(var list: List<Any>) : WheelAdapter<Any> {


    override fun getItemsCount() = list.size

    override fun getItem(index: Int): Any {
        if (index >= 0 && index < list.size) {
            return list[index]
        }
        return ""
    }

    override fun indexOf(o: Any) = list.indexOf(o)
}