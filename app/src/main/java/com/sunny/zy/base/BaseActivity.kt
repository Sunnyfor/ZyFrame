package com.sunny.zy.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.fragment.app.Fragment
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.utils.PlaceholderViewUtil
import com.sunny.zy.utils.ToastUtil
import com.sunny.zy.utils.ToolbarUtil

/**
 * Desc Activity基类
 * Author Zy
 * Date 2018/8/2
 */
@SuppressLint("SourceLockedOrientationActivity")
abstract class BaseActivity : AppCompatActivity(), IBaseView,
    View.OnClickListener, OnTitleListener {

    var taskTag = "DefaultActivity"

    var savedInstanceState: Bundle? = null

    val toolbarUtil: ToolbarUtil by lazy {
        ToolbarUtil()
    }

    private val placeholderViewUtil: PlaceholderViewUtil by lazy {
        PlaceholderViewUtil()
    }

    private lateinit var contentLayout: ContentFrameLayout

    private lateinit var bodyView: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //强制屏幕
        bodyView = FrameLayout(this)
        bodyView.id = R.id.frameBody
        setContentView(bodyView)
        contentLayout = bodyView.parent as ContentFrameLayout

        when (val layoutView = initLayout()) {
            is Int -> {
                if (layoutView != 0) {
                    bodyView.addView(LayoutInflater.from(this).inflate(layoutView, null, false))
                }
            }
            is View -> {
                bodyView.addView(layoutView)
            }

            is Fragment -> {
                supportFragmentManager.beginTransaction()
                    .add(bodyView.id, layoutView).commit()
            }
        }
        initView()
        loadData()
        ZyFrameStore.addActivity(this)
    }


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
    fun getFrameBody(): ViewGroup? = bodyView as ViewGroup


    /**
     * 显示loading覆盖层
     */
    override fun showLoading() {
        placeholderViewUtil.showView(getFrameBody() ?: return, ZyConfig.loadingPlaceholderBean)

    }

    /**
     * 隐藏loading覆盖层
     */
    override fun hideLoading() {
        placeholderViewUtil.hideView(PlaceholderBean.loading)
    }


    fun showPlaceholder(placeholderBean: PlaceholderBean) {
        placeholderViewUtil.showView(getFrameBody() ?: return, placeholderBean)
    }

    /**
     * 显示error覆盖层
     */
    override fun showPlaceholder(viewGroup: ViewGroup, placeholderBean: PlaceholderBean) {
        placeholderViewUtil.showView(viewGroup, placeholderBean)
    }

    /**
     * 隐藏error覆盖层
     */
    override fun hidePlaceholder(placeholderType: Int) {
        placeholderViewUtil.hideView(placeholderType)
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
    override fun titleSimple(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(contentLayout ?: return, bodyView)
        toolbarUtil.titleSimple(title, *menuItem)
    }

    override fun titleCenterSimple(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(contentLayout ?: return, bodyView ?: return, true)
        toolbarUtil.titleSimple(title, *menuItem)
    }

    /**
     * 带返回键的toolbar
     */
    override fun titleDefault(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(contentLayout ?: return, bodyView ?: return)
        toolbarUtil.titleDefault(title, *menuItem)
    }

    override fun titleCenterDefault(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(contentLayout ?: return, bodyView ?: return, true)
        toolbarUtil.titleDefault(title, *menuItem)
    }

    override fun titleSearch(title: String, vararg menuItem: BaseMenuBean) {}


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


    fun setPlaceholderBackground(resInt: Int, viewType: Int) {
        placeholderViewUtil.setBackgroundResources(resInt, viewType)
    }


    override fun onDestroy() {
        super.onDestroy()
        placeholderViewUtil.clear()
        ZyFrameStore.removeActivity(this)
        onClose()
    }
}