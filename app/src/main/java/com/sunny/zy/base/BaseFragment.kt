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
    override fun titleSimple(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().titleSimple(title, *menuItem)
    }

    override fun titleCenterSimple(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().titleCenterSimple(title, *menuItem)
    }

    /**
     * 带返回键的toolbar
     */
    override fun titleDefault(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().titleDefault(title, *menuItem)
    }

    override fun titleCenterDefault(title: String, vararg menuItem: BaseMenuBean) {
        getBaseActivity().titleCenterDefault(title, *menuItem)
    }

    override fun titleCustom(layoutRes: Int) {
        getBaseActivity().titleCustom(layoutRes)
    }

    override fun setStatusColor(color: Int) {
        getBaseActivity().setStatusColor(color)
    }

    override fun setStatusDrawable(drawable: Int, relevantView: View?) {
        getBaseActivity().setStatusDrawable(drawable,relevantView)
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