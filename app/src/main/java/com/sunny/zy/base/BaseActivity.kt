package com.sunny.zy.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.utils.*
import kotlinx.android.synthetic.main.zy_root_layout.*


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
        ToolbarUtil(this)
    }

    val bitmapUtil: BitmapUtil by lazy {
        BitmapUtil()
    }

    private val placeholderViewUtil: PlaceholderViewUtil by lazy {
        PlaceholderViewUtil()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //强制屏幕
        window.statusBarColor = Color.TRANSPARENT

        setStatusBarTextModel(false)
        setContentView(R.layout.zy_root_layout)

        when (val layoutView = initLayout()) {
            is Int -> {
                if (layoutView != 0) {
                    fl_body.addView(LayoutInflater.from(this).inflate(layoutView, null, false))
                }
            }
            is View -> {
                fl_body.addView(layoutView)
            }

            is Fragment -> {
                supportFragmentManager.beginTransaction()
                    .add(fl_body.id, layoutView).commit()
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
    fun getFrameBody(): FrameLayout = fl_body


    /**
     * 显示loading覆盖层
     */
    override fun showLoading() {
        placeholderViewUtil.showView(getFrameBody(), ZyConfig.loadingPlaceholderBean)
    }

    /**
     * 隐藏loading覆盖层
     */
    override fun hideLoading() {
        placeholderViewUtil.hideView(PlaceholderBean.loading)
    }


    fun showPlaceholder(placeholderBean: PlaceholderBean) {
        placeholderViewUtil.showView(getFrameBody(), placeholderBean)
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
    override fun setTitleSimple(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(fl_toolbar)
        toolbarUtil.titleSimple(title, *menuItem)
    }

    override fun setTitleCenterSimple(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(fl_toolbar, R.layout.zy_default_title)
        toolbarUtil.titleSimple(title, *menuItem)
    }

    /**
     * 带返回键的toolbar
     */
    override fun setTitleDefault(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(fl_toolbar)
        toolbarUtil.titleDefault(title, *menuItem)
    }

    override fun setTitleCenterDefault(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(fl_toolbar, R.layout.zy_default_title)
        toolbarUtil.titleDefault(title, *menuItem)
    }

    override fun setTitleCustom(layoutRes: Int) {
        toolbarUtil.initToolbar(fl_toolbar, layoutRes)
    }

    override fun setStatusBarColor(color: Int) {
        view_status_bar.setBackgroundResource(color)
    }

    override fun setStatusBarDrawable(drawable: Int, relevantView: View?) {
        val statusBarHeight = DensityUtil.getStatusBarHeight(this)
        val statusBarBitmap =
            bitmapUtil.getCroppedBitmap(drawable, 0, 0, 0, statusBarHeight)
        view_status_bar.background = (BitmapDrawable(resources, statusBarBitmap))

        var toolbarHeight = 0

        if (fl_toolbar.childCount != 0) {
            toolbarHeight = DensityUtil.getToolBarHeight(this)
            val toolBarBitmap =
                bitmapUtil.getCroppedBitmap(drawable, 0, statusBarHeight, 0, toolbarHeight)
            toolbarUtil.toolbar?.background = (BitmapDrawable(resources, toolBarBitmap))
        }

        relevantView?.let {
            val relevantViewBitmap =
                bitmapUtil.getCroppedBitmap(drawable, 0, statusBarHeight + toolbarHeight, 0, 0)
            it.background = (BitmapDrawable(resources, relevantViewBitmap))
        }
    }

    /**
     * 设置状态栏文字颜色
     *  @param isDark true为黑色 false为白色
     */
    @Suppress("DEPRECATION")
    override fun setStatusBarTextModel(isDark: Boolean) {
        window.decorView.systemUiVisibility = if (isDark) {
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
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


    fun setPlaceholderBackground(resInt: Int, viewType: Int) {
        placeholderViewUtil.setBackgroundResources(resInt, viewType)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolbarUtil.createMenu()
        return super.onCreateOptionsMenu(menu)
    }


    override fun onDestroy() {
        super.onDestroy()
        BitmapUtil().destroy()
        placeholderViewUtil.clear()
        ZyFrameStore.removeActivity(this)
        onClose()
    }
}