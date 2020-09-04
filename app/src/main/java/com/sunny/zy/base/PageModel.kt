package com.sunny.zy.base

/**
 * Desc 分页公共实体类
 * Author Zy
 * Date 2020/6/2 16:23
 */
class PageModel<T> : BaseModel<PageModel.Page<T>>() {

    class Page<T> {
        var totalCount = 0
        var pageSize = 1
        var totalPage = 1
        var currPage = 1
        var list = ArrayList<T>()

        override fun toString(): String {
            return "Page(totalCount=$totalCount, pageSize=$pageSize, totalPage=$totalPage, currPage=$currPage, list=$list)"
        }
    }
}