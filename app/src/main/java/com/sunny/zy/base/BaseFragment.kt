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
    private var placeholderViewUtil: PlaceholderViewUtil? = null

    private lateinit var bodyView: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.savedInstanceState = savedInstanceState

        placeholderViewUtil = PlaceholderViewUtil()

        bodyView = FrameLayout(requireContext())

        when (val layoutView = initLayout()) {
            is Int -> {
                if (layoutView != 0) {
                    bodyView.addView(inflater.inflate(layoutView, container, false))

                }
            }

            is View -> {
                bodyView.addView(layoutView)
            }
        }
        return bodyView
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
        placeholderViewUtil?.showView(
            bodyView,
            PlaceholderBean(PlaceholderBean.loading)
        )
    }

    override fun hideLoading() {
        placeholderViewUtil?.hideView(PlaceholderBean.loading)

    }


    fun showPlaceholder(placeholderBean: PlaceholderBean) {
        showPlaceholder(bodyView as ViewGroup, placeholderBean)
    }

    override fun showPlaceholder(viewGroup: ViewGroup, placeholderBean: PlaceholderBean) {
        placeholderViewUtil?.showView(viewGroup, placeholderBean)

    }

    override fun hidePlaceholder(placeholderType: Int) {
        placeholderViewUtil?.hideView(placeholderType)

    }

    override fun onClick(v: View) {
        onClickEvent(v)
    }


    /**
     * 只有标题的toolbar
     */
    override fun setTitleSimple(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().setTitleSimple(title, *menuItem)
    }

    override fun setTitleCenterSimple(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().setTitleCenterSimple(title, *menuItem)
    }

    /**
     * 带返回键的toolbar
     */
    override fun setTitleDefault(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().setTitleDefault(title, *menuItem)
    }

    override fun setTitleCenterDefault(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().setTitleCenterDefault(title, *menuItem)
    }

    override fun setTitleCustom(layoutRes: Int) {
        getBaseActivity().setTitleCustom(layoutRes)
    }

    override fun setStatusBarColor(color: Int) {
        getBaseActivity().setStatusBarColor(color)
    }

    override fun setStatusBarDrawable(drawable: Int, relevantView: View?) {
        getBaseActivity().setStatusBarDrawable(drawable,relevantView)
    }

    override fun setStatusBarTextModel(isDark: Boolean) {
        getBaseActivity().setStatusBarTextModel(isDark)
    }


    fun setPlaceholderBackground(resInt: Int, viewType: Int) {
        placeholderViewUtil?.setBackgroundResources(resInt, viewType)
    }

    override fun onDestroyView() {
        bodyView.removeAllViews()
        placeholderViewUtil?.clear()
        onClose()
        super.onDestroyView()
    }


    abstract fun onClose()


}