package com.sunny.zy.base

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.FitWindowsLinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sunny.zy.R
import com.sunny.zy.ZyFrameConfig
import com.sunny.zy.ZyFrameStore
import com.sunny.zy.utils.*


/**
 * Desc Activity基类
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2018/8/2
 */
abstract class BaseActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback, IBaseView,
    View.OnClickListener, OnTitleListener {

    var taskTag = "DefaultActivity"

    var savedInstanceState: Bundle? = null

    private var isDark = false

    private var mStatusBarColor: Int = 0

    private val permissionsUtil: PermissionsUtil by lazy {
        PermissionsUtil(1100)
    }

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

    lateinit var frameBody: ViewGroup

    var screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        requestedOrientation = screenOrientation //强制屏幕

        val statusBarParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            DensityUtil.getStatusBarHeight()
        )
        getRootView().addView(statusBar, 0, statusBarParams)
        mStatusBarColor = R.color.colorPrimary
        setStatusBarColor(mStatusBarColor)
        setStatusBarTextModel(ZyFrameConfig.statusBarIsDark)
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
        ZyFrameStore.addActivity(this)
        initView()
        loadData()
    }

    override fun onRestart() {
        super.onRestart()
        if (permissionsUtil.isNoHint) {
            permissionsUtil.retryRequestPermissions(this)
        }
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
        showPlaceholder(frameBody, ZyFrameConfig.loadingPlaceholderBean)
    }

    /**
     * 隐藏loading覆盖层
     */
    override fun hideLoading() {
        hidePlaceholder(ZyFrameConfig.loadingPlaceholderBean.viewType)
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
        setOnClickListener(this, *views)
    }

    fun setOnClickListener(onClick: View.OnClickListener, vararg views: View) {
        views.forEach {
            if (it.getTag(R.id.zy_click_interval) == null) {
                it.setTag(R.id.zy_click_interval, ZyFrameConfig.clickInterval)
            }
            it.setOnClickListener(onClick)
        }
    }

    /**
     * 点击事件处理
     */
    private var lastClickId = 0
    private var lastClickTime = 0L

    fun clickProcess(view: View, onClick: () -> Unit) {
        val tag = view.getTag(R.id.zy_click_interval)
        if (tag == null) {
            onClick.invoke()
        } else {
            var interval = 0L
            if (tag is Int) {
                interval = tag.toLong()
            }

            if (tag is Long) {
                interval = tag
            }

            if (view.id == lastClickId && System.currentTimeMillis() - lastClickTime < interval) {
                return
            }
            lastClickId = view.id
            lastClickTime = System.currentTimeMillis()
            onClick.invoke()
        }
    }


    override fun onClick(view: View) {
        clickProcess(view) {
            onClickEvent(view)
        }
    }

    override fun hideTitle() {
        toolbarUtil.hide()
    }

    override fun showTitle() {
        toolbarUtil.show()
    }


    private fun getRootView() = findViewById<FitWindowsLinearLayout>(androidx.appcompat.R.id.action_bar_root)

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

    override fun setTitleCustom(layoutRes: Int, vararg menuItem: BaseMenuBean) {
        toolbarUtil.initToolbar(getRootView(), layoutRes)
        toolbarUtil.setTitleCustom(layoutRes, *menuItem)
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

    override fun setStatusBarColor(@ColorRes color: Int) {
        mStatusBarColor = color
        statusBar.setBackgroundResource(color)
    }

    override fun setStatusBarDrawable(@DrawableRes drawable: Int, relevantView: View?) {
        mStatusBarColor = drawable
        val width = DensityUtil.screenWidth()
        val statusBarHeight = DensityUtil.getStatusBarHeight()
        var toolbarHeight = 0
        if (toolbar != null) {
            toolbarHeight = DensityUtil.getToolBarHeight()
        }

        if (relevantView == null) {
            bitmapUtil.initBitmap(drawable, width, statusBarHeight + toolbarHeight)
            setStatusBarDrawable(width, statusBarHeight)
            setToolBarDrawable(width, statusBarHeight, toolbarHeight)
        } else {
            relevantView.post {

                val viewHeight = relevantView.height
                if (viewHeight < 1) {
                    throw IllegalArgumentException("relevantView的高度不能小于1")
                }

                bitmapUtil.initBitmap(
                    drawable, width, statusBarHeight + toolbarHeight + viewHeight
                )
                setStatusBarDrawable(width, statusBarHeight)
                setToolBarDrawable(width, statusBarHeight, toolbarHeight)
                val relevantViewBitmap =
                    bitmapUtil.getCroppedBitmap(
                        0, statusBarHeight + toolbarHeight,
                        width,
                        viewHeight
                    )
                relevantView.background = (BitmapDrawable(resources, relevantViewBitmap))
            }
        }
    }

    private fun setStatusBarDrawable(width: Int, statusBarHeight: Int) {
        val statusBarBitmap = bitmapUtil.getCroppedBitmap(0, 0, width, statusBarHeight)
        statusBar.background = (BitmapDrawable(resources, statusBarBitmap))
    }

    private fun setToolBarDrawable(width: Int, statusBarHeight: Int, toolbarHeight: Int) {
        if (toolbar != null) {
            val toolBarBitmap =
                bitmapUtil.getCroppedBitmap(
                    0,
                    statusBarHeight,
                    width,
                    toolbarHeight
                )
            toolbarUtil.toolbar?.background = (BitmapDrawable(resources, toolBarBitmap))
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
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
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

    /**
     * 请求动态多权限
     */
    fun requestPermissions(
        permission: Array<String>,
        permissionOkResult: (() -> Unit)? = null
    ) {
        permissionsUtil.requestPermissions(this, permission, permissionOkResult)
    }

    /**
     * 请求动态权限
     */
    fun requestPermissions(
        permissions: String,
        permissionOkResult: (() -> Unit)? = null
    ) {
        permissionsUtil.requestPermissions(this, permissions, permissionOkResult)
    }

    fun setPermissionsCancelFinish(isFinish: Boolean) {
        permissionsUtil.isCancelFinish = isFinish
    }

    fun setPermissionsNoHintFinish(isFinish: Boolean) {
        permissionsUtil.isNoHintFinish = isFinish
    }

    /**
     * 权限回调
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }


    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            ZyFrameStore.removeActivity(this)
            bitmapUtil.destroy()
            placeholderViewUtil.clear()
            onClose()
        }
    }
}