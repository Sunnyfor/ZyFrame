package com.sunny.zy.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.appcompat.widget.FitWindowsLinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sunny.zy.R
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.utils.*


/**
 * Desc Activity基类
 * Author Zy
 * Date 2018/8/2
 */
@Suppress("MemberVisibilityCanBePrivate")
@SuppressLint("SourceLockedOrientationActivity")
abstract class BaseActivity : AppCompatActivity(), IBaseView,
    View.OnClickListener, OnTitleListener {

    var taskTag = "DefaultActivity"

    var savedInstanceState: Bundle? = null

    private var isDark = false

    private var mStatusBarColor: Int = 0

    private val toolbarUtil: ToolbarUtil by lazy {
        ToolbarUtil(this)
    }

    private val bitmapUtil: BitmapUtil by lazy {
        BitmapUtil()
    }

    val placeholderViewUtil: PlaceholderViewUtil by lazy {
        PlaceholderViewUtil()
    }

    val toolbar: ZyToolBar?
        get() = toolbarUtil.toolbar


    val statusBar: View by lazy {
        View(this)
    }

    lateinit var frameBody:ViewGroup

    var screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        requestedOrientation = screenOrientation //强制屏幕

        val statusBarParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            DensityUtil.getStatusBarHeight(this)
        )
        getRootView().addView(statusBar, 0,statusBarParams)
        mStatusBarColor = R.color.colorPrimary
        setStatusBarColor(mStatusBarColor)
        setStatusBarTextModel(ZyConfig.statusBarIsDark)
        frameBody = findViewById(android.R.id.content)

        when (val layoutView = initLayout()) {
            is Int -> {
                if (layoutView != 0) {
                    setContentView(layoutView)
                }
            }
            is View -> {
                setContentView(layoutView)
            }

            is Fragment -> {
                supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, layoutView).commit()
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
     * 显示loading覆盖层
     */
    override fun showLoading() {
        showPlaceholder(frameBody, ZyConfig.loadingPlaceholderBean)
    }

    /**
     * 隐藏loading覆盖层
     */
    override fun hideLoading() {
        hidePlaceholder(ZyConfig.loadingPlaceholderBean.viewType)
    }


    fun showPlaceholder(placeholderBean: PlaceholderBean) {
        placeholderViewUtil.showView(frameBody, placeholderBean)
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
    override fun hidePlaceholder(viewType: Int) {
        placeholderViewUtil.hideView(viewType)
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

    override fun hideTitle() {
        toolbarUtil.hide()
    }

    override fun showTitle() {
        toolbarUtil.show()
    }


    private fun getRootView() = findViewById<FitWindowsLinearLayout>(R.id.action_bar_root)

    /**
     * 只有标题的toolbar
     */
    override fun setTitleSimple(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(getRootView())
        toolbarUtil.titleSimple(title, *menuItem)
    }

    override fun setTitleCenterSimple(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(getRootView(), R.layout.zy_default_title)
        toolbarUtil.titleSimple(title, *menuItem)
    }

    /**
     * 带返回键的toolbar
     */
    override fun setTitleDefault(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(getRootView())
        toolbarUtil.titleDefault(title, *menuItem)
    }

    override fun setTitleCenterDefault(title: String, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(getRootView(), R.layout.zy_default_title)
        toolbarUtil.titleDefault(title, *menuItem)
    }

    override fun setTitleCustom(layoutRes: Int) {
        toolbarUtil.initToolbar(getRootView(), layoutRes)
    }

    override fun setTitleBackground(textColor: Int, backgroundColor: Int) {
        if (backgroundColor != 0) {
            toolbar?.setBackgroundResource(backgroundColor)
            setStatusBarColor(backgroundColor)
        }
        if (textColor != 0) {
            toolbar?.setTitleTextColor(ContextCompat.getColor(this, textColor))
        }
    }

    override fun setStatusBarColor(color: Int) {
        mStatusBarColor = color
        statusBar.setBackgroundResource(color)
    }

    override fun setStatusBarDrawable(drawable: Int, relevantView: View?) {

        mStatusBarColor = drawable

        val statusBarHeight = DensityUtil.getStatusBarHeight(this)
        val statusBarBitmap =
            bitmapUtil.getCroppedBitmap(drawable, 0, 0, 0, statusBarHeight)
        statusBar.background = (BitmapDrawable(resources, statusBarBitmap))

        var toolbarHeight = 0

        if (toolbar != null) {
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
        this.isDark = isDark
        window.decorView.systemUiVisibility = if (isDark) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    @Suppress("DEPRECATION")
    override fun showStatusBar(showText: Boolean?) {
        if (showText == true) {
            setStatusBarTextModel(isDark)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        statusBar.visibility = View.VISIBLE
    }


    override fun hideStatusBar(showText: Boolean?) {
        showStatusBar(showText)
        statusBar.visibility = View.GONE
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