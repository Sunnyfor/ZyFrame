package com.sunny.zy.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
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

/**
 * Desc Activity基类
 * Author Zy
 * Date 2018/8/2
 */
@SuppressLint("SourceLockedOrientationActivity")
abstract class BaseActivity : AppCompatActivity(), IBaseView,
    View.OnClickListener, OnTitleListener {

    var taskTag = "DefaultActivity"

    var toolbar: ZyToolBar? = null

    var savedInstanceState: Bundle? = null

    var menuList = ArrayList<BaseMenuBean>()

    private var isCustomToolbar = false

    private val placeholderViewUtil = PlaceholderViewUtil()

    private var contentLayout: ContentFrameLayout? = null

    private var bodyView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //强制屏幕
        val viewStub = ViewStub(this)
        setContentView(viewStub)
        contentLayout = viewStub.parent as ContentFrameLayout
        when (val layoutView = initLayout()) {
            is Int -> {
                if (layoutView != 0) {
                    bodyView = LayoutInflater.from(this).inflate(layoutView, null, false)
                    contentLayout?.removeView(viewStub)
                    contentLayout?.addView(bodyView)
                }
            }
            is View -> {
                bodyView = layoutView
                contentLayout?.removeView(viewStub)
                contentLayout?.addView(bodyView)
            }

            is Fragment -> {
                bodyView = FrameLayout(this)
                bodyView?.id = R.id.frameContent
                contentLayout?.removeView(viewStub)
                contentLayout?.addView(bodyView)
                supportFragmentManager.beginTransaction()
                    .add(bodyView?.id ?: return, layoutView).commit()
            }
        }

        initView()
        loadData()

        ZyFrameStore.addActivity(this)
    }


    @SuppressLint("PrivateResource")
    private fun initTitle() {
        if (toolbar == null) {
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
            )
            toolbar = ZyToolBar(this, isCustomToolbar)
            contentLayout?.addView(toolbar, layoutParams)
            (contentLayout?.getChildAt(0)?.layoutParams as FrameLayout.LayoutParams).topMargin =
                resources.getDimension(R.dimen.abc_action_bar_default_height_material).toInt()
            setSupportActionBar(toolbar)
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
        initTitle()
        setTitle(title)
        toolbar?.navigationIcon = null
        toolbar?.setNavigationOnClickListener(null)
        createMenu(*menuItem)
    }

    override fun titleCenterSimple(title: String, vararg menuItem: BaseMenuBean) {
        isCustomToolbar = true
        titleSimple(title, *menuItem)
    }

    /**
     * 带返回键的toolbar
     */
    override fun titleDefault(title: String, vararg menuItem: BaseMenuBean) {
        titleSimple(title, *menuItem)
        toolbar?.setNavigationIcon(R.drawable.svg_title_back)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
    }

    override fun titleCenterDefault(title: String, vararg menuItem: BaseMenuBean) {
        isCustomToolbar = true
        titleDefault(title, *menuItem)
    }

    override fun titleSearch(title: String, vararg menuItem: BaseMenuBean) {}


    /**
     * 创建toolbar菜单
     */
    fun createMenu(vararg menuItem: BaseMenuBean) {
        menuList.clear()
        menuList.addAll(menuItem)
    }

    /**
     * 清理Toolbar菜单
     */
    open fun clearMenu() {
        menuList.clear()
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


    fun setPlaceholderBackground(resInt: Int, viewType: Int) {
        placeholderViewUtil.setBackgroundResources(resInt, viewType)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolbar?.addMenu(menuList)
        return true
    }

    fun setSupportActionBar(toolbar: ZyToolBar?) {
        this.toolbar = toolbar
        super.setSupportActionBar(toolbar)
    }


    override fun onDestroy() {
        super.onDestroy()
        placeholderViewUtil.clear()
        ZyFrameStore.removeActivity(this)
        onClose()
    }
}