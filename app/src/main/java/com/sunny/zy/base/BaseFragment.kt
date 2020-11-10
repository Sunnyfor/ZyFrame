package com.sunny.zy.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.sunny.zy.utils.PlaceholderViewUtil


/**
 *
 * Created by Zy on 2018/8/2.
 */
/**
 * Desc Fragment基类
 * Author Zy
 * Date 2018/8/2
 */
abstract class BaseFragment : Fragment(), IBaseView, View.OnClickListener, OnTitleListener {
    private var savedInstanceState: Bundle? = null
    private val placeholderViewUtil = PlaceholderViewUtil()
    private var rootView: FrameLayout? = null
    private var bodyView: View? = null

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
                    bodyView = inflater.inflate(layoutView, container, false)

                }
            }

            is View -> {
                bodyView = layoutView
            }
        }
        rootView?.addView(bodyView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
        getBaseActivity().onFragmentLoadFinish(this)
    }


    open fun getBaseActivity(): BaseActivity = requireActivity() as BaseActivity

    /**
     * 批量注册点击事件
     * @param views 注册事件的View
     */
    open fun setOnClickListener(vararg views: View, onClickListener: View.OnClickListener? = this) {
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
        if (bodyView is ViewGroup) {
            placeholderViewUtil.showView(
                rootView as ViewGroup,
                PlaceholderBean(PlaceholderBean.loading)
            )
        }
    }

    override fun hideLoading() {
        if (bodyView is ViewGroup) {
            placeholderViewUtil.hideView(PlaceholderBean.loading)
        }
    }


    fun showPlaceholder(placeholderBean: PlaceholderBean) {
        showPlaceholder(bodyView as ViewGroup, placeholderBean)
    }

    override fun showPlaceholder(viewGroup: ViewGroup, placeholderBean: PlaceholderBean) {
        if (bodyView is ViewGroup) {
            placeholderViewUtil.showView(viewGroup, placeholderBean)
        }
    }

    override fun hidePlaceholder(placeholderType: Int) {
        if (bodyView is ViewGroup) {
            placeholderViewUtil.hideView(placeholderType)
        }
    }

    override fun onClick(v: View) {
        onClickEvent(v)
    }


    /**
     * 只有标题的toolbar
     */
    override fun titleSimple(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().let {
            it.toolbarUtil.initToolbar(rootView ?: return, bodyView ?: return)
            it.toolbarUtil.titleSimple(title, *menuItem)
        }
    }

    override fun titleCenterSimple(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().let {
            it.toolbarUtil.initToolbar(rootView ?: return, bodyView ?: return, true)
            it.toolbarUtil.titleSimple(title, *menuItem)
        }
    }

    /**
     * 带返回键的toolbar
     */
    override fun titleDefault(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().let {
            it.toolbarUtil.initToolbar(rootView ?: return, bodyView ?: return)
            it.toolbarUtil.titleDefault(title, *menuItem)
        }
    }

    override fun titleCenterDefault(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().let {
            it.toolbarUtil.initToolbar(rootView ?: return, bodyView ?: return, true)
            it.toolbarUtil.titleDefault(title, *menuItem)
        }
    }

    override fun titleSearch(title: String, vararg menuItem: BaseMenuBean) {}


    fun setPlaceholderBackground(resInt: Int, viewType: Int) {
        placeholderViewUtil.setBackgroundResources(resInt, viewType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getBaseActivity().toolbarUtil.onDestroy(rootView)
        onClose()
    }


    abstract fun onClose()


}