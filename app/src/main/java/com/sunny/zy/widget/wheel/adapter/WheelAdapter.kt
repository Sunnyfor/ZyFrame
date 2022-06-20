package com.sunny.zy.widget.wheel.adapter


/**
 * Desc
 * Author ZY
 * Date 2022/5/14
 */
interface WheelAdapter {
    /**
     * Gets items count
     * @return the count of wheel items
     */
    fun getItemsCount(): Int

    /**
     * Gets a wheel item by index.
     * @param index the item index
     * @return the wheel item text or null
     */
    fun getItem(index: Int): Any

    /**
     * Gets maximum item length. It is used to determine the wheel width.
     * If -1 is returned there will be used the default wheel width.
     * @param o  the item object
     * @return the maximum item length or -1
     */
    fun indexOf(o: Any): Int
}