package com.sunny.zy.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.sunny.zy.widget.utils.OverlayViewUtils


/**
 *
 * Created by zhangye on 2018/8/2.
 */
abstract class BaseFragment : Fragment(), IBaseView, View.OnClickListener {
    private var savedInstanceState: Bundle? = null

    private val overlayViewBean = OverlayViewUtils()
    private var rootView: FrameLayout? = null
    var bodyView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.savedInstanceState = savedInstanceState
        rootView = FrameLayout(requireContext())
        if (setLayout() != 0) {
            bodyView = inflater.inflate(setLayout(), container, false)
            rootView?.addView(bodyView)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadData()
    }


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


    abstract fun setLayout(): Int

    abstract fun initView()

    abstract fun onClickEvent(view: View)

    abstract fun loadData()

    fun setLayoutView(view: View) {
        rootView?.let {
            if (view.parent != null) {
                (view.parent as ViewGroup).removeView(view)
            }
            it.addView(view)
        }
    }

    override fun showMessage(message: String) {
        getBaseActivity().showMessage(message)
    }

    override fun showLoading() {
        overlayViewBean.showView(rootView ?: return, ErrorViewType.loading)
    }

    override fun hideLoading() {
        overlayViewBean.hideView(rootView ?: return, ErrorViewType.loading)
    }

    override fun showError(errorType: ErrorViewType) {
        overlayViewBean.showView(rootView ?: return, ErrorViewType.networkError)
    }

    override fun hideError(errorType: ErrorViewType) {
        overlayViewBean.hideView(rootView ?: return, ErrorViewType.networkError)
    }

    override fun onClick(v: View) {
        onClickEvent(v)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        close()
    }

    abstract fun close()


}