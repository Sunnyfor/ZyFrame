package com.sunny.zy.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionBarContainer
import androidx.appcompat.widget.ActionBarOverlayLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.utils.PlaceholderViewUtil
import com.sunny.zy.utils.ToastUtil

/**
 * Desc Activity基类
 * Author Zy
 * Date 2018/8/2
 */
@SuppressLint("SourceLockedOrientationActivity")
abstract class BaseActivity : AppCompatActivity(), IBaseView,
    View.OnClickListener {

    var taskTag = "DefaultActivity"

    var toolbar: Toolbar? = null

    var savedInstanceState: Bundle? = null

    private val overlayViewBeanUtil = PlaceholderViewUtil()

    private var contentLayout: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //强制屏幕
        val viewStub = ViewStub(this)
        setContentView(viewStub)
        initSysLayout()
        initTitle()
        when (val layoutView = initLayout()) {
            is Int -> {
                if (layoutView != 0) {
                    viewStub.layoutResource = layoutView
                    viewStub.inflate()
                }
            }
            is View -> {
                contentLayout?.removeView(viewStub)
                contentLayout?.addView(layoutView)
            }

            is Fragment -> {
                contentLayout?.removeView(viewStub)
                supportFragmentManager.beginTransaction()
                    .add(contentLayout?.id ?: return, layoutView).commit()
            }
        }
        initView()
        loadData()

        ZyFrameStore.addActivity(this)
    }

    private fun initSysLayout() {
        val sysLinearLayout = (window.decorView as ViewGroup).getChildAt(0) as LinearLayout
        var sysFrameLayout: FrameLayout? = null

        findFrame@ for (i in 0 until sysLinearLayout.childCount) {
            val childView = sysLinearLayout.getChildAt(i)
            if (childView is FrameLayout) {
                sysFrameLayout = childView
                break@findFrame
            }
        }
        val actionBarOverlayLayout =
            sysFrameLayout?.findViewById<ActionBarOverlayLayout>(R.id.decor_content_parent)

        val actionBarContainer =
            actionBarOverlayLayout?.findViewById<ActionBarContainer>(R.id.action_bar_container)
        toolbar = actionBarContainer?.findViewById(R.id.action_bar)
        contentLayout = actionBarOverlayLayout?.findViewById(android.R.id.content)
    }

    open fun initTitle() {}

    /**
     * 设置布局操作
     */
    abstract fun initLayout(): Any

    /**
     * 初始化View操作
     */
    abstract fun initView()

    /**
     * 加载数据操作
     */
    abstract fun loadData()

    /**
     * 点击事件回调
     */
    abstract fun onClickEvent(view: View)

    abstract fun onClose()


    /**
     * 获取body容器
     */
    fun getFrameBody(): FrameLayout? = contentLayout


    /**
     * 显示loading覆盖层
     */
    override fun showLoading() {
        overlayViewBeanUtil.showView(getFrameBody() ?: return, ZyConfig.loadingPlaceholderBean)

    }

    /**
     * 隐藏loading覆盖层
     */
    override fun hideLoading() {
        overlayViewBeanUtil.hideView(getFrameBody() ?: return, PlaceholderBean.loading)
    }

    /**
     * 显示error覆盖层
     */
    override fun showPlaceholder(viewGroup: ViewGroup?, placeholderBean: PlaceholderBean) {
        overlayViewBeanUtil.showView(viewGroup ?: getFrameBody() ?: return, placeholderBean)
    }

    /**
     * 隐藏error覆盖层
     */
    override fun hidePlaceholder(overlayViewType: Int) {
        overlayViewBeanUtil.hideView(getFrameBody() ?: return, overlayViewType)
    }

    /**
     * 显示Toast
     */
    override fun showMessage(message: String) {
        ToastUtil.show(message)
    }


    /**
     * 批量注册点击事件
     * @param views 注册事件的View
     */
    fun setOnClickListener(vararg views: View) {
        views.forEach {
            it.setOnClickListener(this)
        }
    }

    /**
     * 点击事件处理
     */
    override fun onClick(view: View) {
        onClickEvent(view)
    }


    /**
     * 只有标题的toolbar
     */
    fun simpleTitle(title: String, vararg menuItem: BaseMenuBean) {
        setTitle(title)
        toolbar?.navigationIcon = null
        toolbar?.setNavigationOnClickListener(null)
        createMenu(*menuItem)
    }

    /**
     * 带返回键的toolbar
     */
    fun defaultTitle(title: String, vararg menuItem: BaseMenuBean) {
        setTitle(title)
        toolbar?.setNavigationIcon(R.drawable.svg_title_back)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        createMenu(*menuItem)
    }

    /**
     * 创建toolbar菜单
     */
    private fun createMenu(vararg menuItem: BaseMenuBean) {
        menuItem.forEach { bean ->
            toolbar?.menu?.add(bean.title)?.let { menuItem ->
                menuItem.setOnMenuItemClickListener {
                    bean.onClickListener.invoke()
                    return@setOnMenuItemClickListener true
                }
                menuItem.setIcon(bean.icon)
                menuItem.setShowAsAction(bean.showAsAction)
            }
        }
    }

    /**
     * 显示标题栏
     */
    fun showTitle() {
        supportActionBar?.show()
    }

    /**
     * 隐藏标题栏
     */
    fun hideTitle() {
        supportActionBar?.hide()
    }

    /**
     * 清理标题菜单
     */
    fun clearMenu() {
        toolbar?.menu?.clear()
    }


    /**
     * 隐藏输入法键盘
     */
    fun hideKeyboard() {
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(
            this.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    /**
     * fragment加载完成后进行回调
     */
    open fun onFragmentLoadFinish(fragment: Fragment) {}


    override fun onDestroy() {
        super.onDestroy()
        onClose()
        ZyFrameStore.removeActivity(this)
    }
}