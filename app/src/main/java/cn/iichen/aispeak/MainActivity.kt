package cn.iichen.aispeak

import android.animation.ValueAnimator
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import cn.iichen.aispeak.service.AiSpeakService
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.NotificationUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ImmersionBar
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var receiver: MainActivity.ServiceStatusReceiver? = null
    open var animator:ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .transparentNavigationBar()
            .navigationBarDarkIcon(true)
            .init()

        setContentView(R.layout.activity_main)

        initAnimator()

        initBroadCast()

        initEvent()

        NotificationUtils.notify(0,NotificationUtils.ChannelConfig("1","无障碍保活",
            NotificationUtils.IMPORTANCE_DEFAULT)) { param ->
            param.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("无障碍")
                .setContentText("点击您需要识别的文本，它将会读给你听！")
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(false)
                .setOngoing(true)
        }

        Beta.checkUpgrade()
    }

    private fun initBroadCast() {
        receiver = ServiceStatusReceiver()
        val intentFilter: IntentFilter = IntentFilter()
        intentFilter.addAction("$packageName.ServiceStatusReceiver")
        registerReceiver(receiver,intentFilter)
    }

    private fun initEvent() {
        val currentVersion = resources.getString(R.string.current_version)
        version.text = String.format(currentVersion,AppUtils.getAppVersionName())
        image.setOnClickListener {
            if (AiSpeakService.isStart){
                ToastUtils.showShort("服务已开启")
            }else{
                jumpToSetting()
            }
        }
        checkVersion.setOnClickListener {
            Beta.checkUpgrade()
        }
        switch_.isChecked = SPUtils.getInstance().getBoolean("switch",true)
        switch_.setOnCheckedChangeListener { buttonView, isChecked ->
            SPUtils.getInstance().put("switch",isChecked)
        }
    }

    private fun jumpToSetting() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            startActivity(intent)
        } catch (e: Exception) {
            startActivity(Intent(Settings.ACTION_SETTINGS))
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if(!AiSpeakService.isStart && SPUtils.getInstance().getBoolean("switch",true)
        ){
            jumpToSetting()
        }
    }

    fun initAnimator(){
        animator = ValueAnimator.ofFloat(0f,360f)
        animator?.run {
            addUpdateListener {
                val value:Float = animatedValue as Float
                image.rotation = value
            }
            duration = 850
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
    }

    fun onStatusChange(open: Boolean) {
        animator?.run {
            if (open){
                start()
                status.text = "运行中..."
                ToastUtils.showShort("服务已开启！")
            } else {
                cancel()
                status.text = "未开启"
                ToastUtils.showShort("服务已关闭！")
            }
        }
    }

    inner class ServiceStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            //拿到进度，更新UI
            val progress = intent.getBooleanExtra("service_status", false)
            onStatusChange(progress)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}