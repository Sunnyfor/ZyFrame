package com.sunny.zy.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.sunny.zy.utils.OverlayViewUtil
import com.sunny.zy.utils.ToastUtil


/**
 *
 * Created by Zy on 2018/8/2.
 */
/**
 * Desc Fragment基类
 * Author Zy
 * Date 2018/8/2
 */
abstract class BaseFragment : Fragment(), IBaseView, View.OnClickListener {
    private var savedInstanceState: Bundle? = null

    private val overlayViewBean = OverlayViewUtil()
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.savedInstanceState = savedInstanceState
        rootView = FrameLayout(requireContext())

        when (val layoutView = initLayout()) {
            is Int -> {
                if (layoutView != 0) {
                    rootView = inflater.inflate(layoutView, container, false)
                }
            }

            is View -> {
                rootView = layoutView
            }
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onFragmentCreate(savedInstanceState)
    }

    open fun onFragmentCreate(savedInstanceState: Bundle?) {}

    fun getBaseActivity(): BaseActivity = requireActivity() as BaseActivity


    /**
     * 批量注册点击事件
     * @param views 注册事件的View
     */
    fun setOnClickListener(vararg views: View, onClickListener: View.OnClickListener? = this) {
        views.forEach {
            it.setOnClickListener(onClickListener)
        }
    }


    abstract fun initLayout(): Any

    abstract fun initView()

    abstract fun onClickEvent(view: View)

    abstract fun loadData()


    override fun showMessage(message: String) {
        getBaseActivity().showMessage(message)
    }

    override fun showLoading() {
        if (rootView is ViewGroup) {
            overlayViewBean.showView(rootView as ViewGroup, ErrorViewType.loading)
        }
    }

    override fun hideLoading() {
        if (rootView is ViewGroup) {
            overlayViewBean.hideView(rootView as ViewGroup, ErrorViewType.loading)
        }
    }

    override fun showError(errorType: ErrorViewType) {
        if (rootView is ViewGroup) {
            overlayViewBean.showView(rootView as ViewGroup, ErrorViewType.error)
        }
    }

    override fun hideError(errorType: ErrorViewType) {
        if (rootView is ViewGroup) {
            overlayViewBean.hideView(rootView as ViewGroup, ErrorViewType.error)
        }
    }

    override fun onClick(v: View) {
        onClickEvent(v)
    }


    fun simpleTitle(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().let {
            it.clearMenu()
            it.simpleTitle(title, *menuItem)
        }
    }


    fun defaultTitle(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().let {
            it.clearMenu()
            it.defaultTitle(title, *menuItem)
        }
    }

    fun showTitle() {
        getBaseActivity().let {

        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        ToastUtil.show("我成功的依芙拉")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        close()
    }

    abstract fun close()


}