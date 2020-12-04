package com.sunny.zy.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sunny.zy.http.ZyConfig
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


    var placeholderViewUtil: PlaceholderViewUtil? = null

    val toolbar: ZyToolBar?
        get() = getBaseActivity().toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.savedInstanceState = savedInstanceState

        placeholderViewUtil = PlaceholderViewUtil()

        val layoutView = initLayout()

        if (layoutView is Int) {
            return inflater.inflate(layoutView, container, false)
        }

        if (layoutView is View) {
            return layoutView
        }

        return null
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


    abstract fun initLayout(): Any?

    abstract fun initView()

    abstract fun onClickEvent(view: View)

    abstract fun loadData()


    override fun showMessage(message: String) {
        getBaseActivity().showMessage(message)
    }


    override fun showLoading() {
        if (view is ViewGroup) {
            showPlaceholder(
                view as ViewGroup,
                ZyConfig.loadingPlaceholderBean
            )
        }

    }

    override fun hideLoading() {
        hidePlaceholder(ZyConfig.loadingPlaceholderBean.viewType)
    }


    fun showPlaceholder(placeholderBean: PlaceholderBean) {
        if (view is ViewGroup) {
            showPlaceholder(view as ViewGroup, placeholderBean)
        }
    }

    override fun showPlaceholder(viewGroup: ViewGroup, placeholderBean: PlaceholderBean) {
        placeholderViewUtil?.showView(viewGroup, placeholderBean)

    }

    override fun hidePlaceholder(viewType: Int) {
        placeholderViewUtil?.hideView(viewType)
    }

    override fun onClick(v: View) {
        onClickEvent(v)
    }


    override fun hideTitle() {
        getBaseActivity().hideTitle()
    }

    override fun showTitle() {
        getBaseActivity().showTitle()
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

    override fun setTitleBackground(textColor: Int, backgroundColor: Int) {
        getBaseActivity().setTitleBackground(textColor, backgroundColor)
    }

    override fun setStatusBarColor(color: Int) {
        getBaseActivity().setStatusBarColor(color)
    }

    override fun setStatusBarDrawable(drawable: Int, relevantView: View?) {
        getBaseActivity().setStatusBarDrawable(drawable, relevantView)
    }

    override fun setStatusBarTextModel(isDark: Boolean) {
        getBaseActivity().setStatusBarTextModel(isDark)
    }

    override fun showStatusBar(showText: Boolean?) {
        getBaseActivity().showStatusBar(showText)
    }

    override fun hideStatusBar(showText: Boolean?) {
        getBaseActivity().hideStatusBar(showText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        placeholderViewUtil?.clear()
        placeholderViewUtil = null
        onClose()
    }


    abstract fun onClose()


}