package com.sunny.zy.gallery.contract

import com.sunny.zy.base.BasePresenter
import com.sunny.zy.base.IBaseView
import com.sunny.zy.gallery.bean.GalleryFolderBean
import com.sunny.zy.gallery.model.GalleryModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Desc
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2021/9/23 09:29
 */
class GalleryContract {
    interface IView : IBaseView {
        fun showGalleryData(data: List<GalleryFolderBean>)
    }

    class Presenter(view: IView) : BasePresenter<IView>(view) {
        private val model: GalleryModel by lazy {
            GalleryModel()
        }

        //加载图片和视频
        fun loadImageAndVideData() {
            view?.showLoading()
            val result = arrayListOf<GalleryFolderBean>()
            launch {
                withContext(IO) {
                    result.addAll(model.getImageAndVideFolder())
                }
                view?.hideLoading()
                view?.showGalleryData(result)
            }

        }

        fun loadImageData() {
            val result = arrayListOf<GalleryFolderBean>()
            launch {
                withContext(IO) {
                    result.addAll(model.getImageFolder())
                }
                view?.hideLoading()
                view?.showGalleryData(result)
            }
        }


        fun loadVideoData() {
            val result = arrayListOf<GalleryFolderBean>()
            launch {
                withContext(IO) {
                    result.addAll(model.getVideoFolder())
                }
                view?.hideLoading()
                view?.showGalleryData(result)
            }
        }
    }
}