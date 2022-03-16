package cn.iichen.aispeak

import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import cn.iichen.aispeak.service.AiSpeakService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var receiver: MainActivity.ServiceStatusReceiver? = null
    open var animator:ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAnimator()

        initBroadCast()

        initEvent()
    }

    private fun initBroadCast() {
        receiver = ServiceStatusReceiver()
        val intentFilter: IntentFilter = IntentFilter()
        intentFilter.addAction("$packageName.ServiceStatusReceiver")
        registerReceiver(receiver,intentFilter)
    }

    private fun initEvent() {
        image.setOnClickListener {
            if (AiSpeakService.isStart){

            }else{
                try {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    startActivity(intent)
                } catch (e: Exception) {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                    e.printStackTrace()
                }
            }
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
            } else {
                cancel()
                status.text = "未开启"
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