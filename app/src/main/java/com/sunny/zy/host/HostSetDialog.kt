package com.sunny.zy.host

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunny.zy.R
import com.sunny.zy.http.ZyConfig
import com.sunny.zy.utils.SpUtil
import com.sunny.zy.utils.ToastUtil
import kotlinx.android.synthetic.main.dialog_host_set.view.*


/**
 * Desc 域名设置对话框
 * Author ZY
 * Mail sunnyfor98@gmail.com
 * Date 2020年3月7日 21:32:59
 */
class HostSetDialog(context: Context, var defaultHostList: ArrayList<String>) : AlertDialog(context) {

    private val mView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.dialog_host_set, null, false)
    }

    private var hostList = ArrayList<String>()

    private val hostAdapter = HostAdapter(hostList).apply {
        setOnItemClickListener { _, position ->
            mView.et_input_host.setText(hostList[position])
        }
        onItemDeleteListener = { position ->
            delHostItem(position)
        }
    }


    init {
        setTitle("接口地址配置")
        setView(mView)

        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setGravity(Gravity.BOTTOM)

        setCanceledOnTouchOutside(false)
        setCancelable(false)

        mView.recyclerView.layoutManager = LinearLayoutManager(context)
        mView.recyclerView.adapter = hostAdapter

        mView.et_input_host.clearFocus()
        SpUtil.get(HostKey.host_fileName).getString(HostKey.host_config).let {
            mView.et_input_host.setText(if (it.isEmpty()) ZyConfig.HOST else it)
        }

        mView.btn_save.setOnClickListener {
            val host = mView.et_input_host.text.toString().trim()
            if (host.isEmpty()) {
                ToastUtil.show("请输入接口地址")
            } else {
                dismiss()
                if (!hostList.contains(host)) {
                    addHostList(host)
                }
                ZyConfig.HOST = host
                SpUtil.get(HostKey.host_fileName).setString(HostKey.host_config, host)
            }
        }
    }

    override fun show() {
        super.show()
        loadHostList()
    }


    /**
     * 加载 host列表
     */
    private fun loadHostList() {
        hostList.clear()

        val json = SpUtil.get(HostKey.host_fileName).getString(HostKey.host_list)
        if (json != "") {
            hostList.addAll(Gson().fromJson(json, object : TypeToken<List<String>>() {}.type))
        } else {
            hostList.addAll(defaultHostList)
            SpUtil.get(HostKey.host_fileName).setString(HostKey.host_list, Gson().toJson(hostList))
        }
        hostAdapter.notifyDataSetChanged()

    }


    /**
     * 添加 host
     */
    private fun addHostList(host: String) {
        hostList.add(0, host)
        hostAdapter.notifyDataSetChanged()
        SpUtil.get(HostKey.host_fileName).setString(HostKey.host_list, Gson().toJson(hostList))
    }

    /**
     * 删除 host ：先将sp中的字符串转为list，删除相对应的子条目，在存入sp中
     */
    private fun delHostItem(position: Int) {
        hostList.removeAt(position)
        hostAdapter.notifyDataSetChanged()

        val spList = Gson().fromJson<ArrayList<String>>(
            SpUtil.get(HostKey.host_fileName).getString(HostKey.host_list, ""),
            object : TypeToken<List<String>>() {}.type
        )
        spList.removeAt(position)
        SpUtil.get(HostKey.host_fileName).setString(HostKey.host_list, Gson().toJson(spList))
    }
}