package cn.iichen.aispeak.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.text.TextShaper
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import org.w3c.dom.Text
import java.util.*

class AiSpeakService : AccessibilityService() {
    private var textSpeech: TextToSpeech? = null
    private var intent: Intent? = null
    private val TAG = javaClass.name

    //初始化
    override fun onServiceConnected() {
        super.onServiceConnected()
        mService = this
        openServiceHandler()

        textSpeech = textSpeech ?: TextToSpeech(applicationContext) {
            if(it == TextToSpeech.SUCCESS){
                textSpeech?.run {
                    val result = setLanguage(Locale.CHINA)
                    if(result != TextToSpeech.LANG_AVAILABLE && result!= TextToSpeech.LANG_COUNTRY_AVAILABLE){
                        Toast.makeText(applicationContext,"暂不支持中文",Toast.LENGTH_SHORT).show()
                        Log.d("iichen", "######### 暂不支持中文")
                    }
                }
            }else{
                Log.d("iichen", "######### 初始化失败")
            }
        }
    }

    private fun openServiceHandler() {
        intent = Intent("$packageName.ServiceStatusReceiver")
        intent?.run {
            putExtra("service_status", true)
            sendBroadcast(intent)
        }
        ToastUtils.showShort("服务已开启！")
    }

    //实现辅助功能
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//        event.packageName.toString()
        event?.run {
            when (eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
//                    不需要要页面变更时 处理了
//                    when (packageName){
//                          微信
//                        "com.tencent.mm" -> wxWindowChangeHandler(event)
//                          系统短信
//                        "com.android.mms" -> mmsWindowChangeHandler(event)
//                        else -> {
//
//                        }
//                    }
                }
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    // className == "android.view.View"  文本在这上面 后续处理 咸鱼就是


                    // 获取事件源对象 node
                    val node = event.source
                    node?.run {
                        // TextView直接获取
                        val clickNode = getClickAbleNodeInfo(this)
                        if(clickNode.className == "android.widget.TextView"){
                            Log.d("iichen", "######### 页面TextView元素点击 ${node.text}")
                            TextToSpeak(node.text)
                        }else{
                            // 被包裹的父组件 遍历获取下面的 TextView 读取
                            for(index in 0 until clickNode.childCount){
                                val nodeInfo = clickNode.getChild(index)
                                nodeInfo?.run {
                                    if(className == "android.widget.TextView"){
                                        Log.d("iichen", "######### 页面非TextView点击 $text")
                                        TextToSpeak(text)
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {

                }
            }

        }
    }

    private fun TextToSpeak(text: CharSequence?) {
        textSpeech?.speak(text.toString(),TextToSpeech.QUEUE_FLUSH,null,text.toString())
    }

    private fun getClickAbleNodeInfo(nodeInfo: AccessibilityNodeInfo): AccessibilityNodeInfo {
        nodeInfo.run {
            if (isClickable){
                return nodeInfo
            }else {
                if(parent!=null)
                    getClickAbleNodeInfo(nodeInfo.parent)
                else{
                    return nodeInfo
                }
            }
        }
        return nodeInfo
    }

    private fun mmsWindowChangeHandler(event: AccessibilityEvent) {
        val rootWindow = rootInActiveWindow
    }

    private fun wxWindowChangeHandler(event: AccessibilityEvent) {
        val rootWindow = rootInActiveWindow
        // 聊天页所有的 聊天文本对应的 ID组件查询
        // val nodeList = rootWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b4b")

        val nodeDoubleClickList =
            rootWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eh_")
        if (nodeDoubleClickList.size > 0) {
            for (info in nodeDoubleClickList) {
                Log.d("iichen", "######### 双击页 ${info.text}")
            }
        } else {
        }
    }

    override fun onInterrupt() {
        closeServiceHandler()

    }

    override fun onDestroy() {
        super.onDestroy()
        closeServiceHandler()
        textSpeech?.run {
            stop()
            shutdown()
            null
        }
    }

    private fun closeServiceHandler() {
        mService = null
        intent?.run {
            putExtra("service_status", false)
            sendBroadcast(intent)
        }
        ToastUtils.showShort("服务已关闭！")
    }

    companion object {
        var mService: AiSpeakService? = null
        // 公共方法
        /**
         * 辅助功能是否启动
         */
        val isStart: Boolean
            get() = mService != null

    }
}