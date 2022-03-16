package cn.iichen.aispeak.dao

import android.net.Uri
import androidx.core.app.NotificationCompat

/**
 *
 * @ProjectName:    AiSpeak
 * @Package:        cn.iichen.aispeak.dao
 * @ClassName:      Channel
 * @Description:     java类作用描述
 * @Author:         作者名 qsdiao
 * @CreateDate:     2022/3/16 20:02
 * @UpdateUser:     更新者：qsdiao
 * @UpdateDate:     2022/3/16 20:02
 * @UpdateRemark:   更新说明：Fuck code, go to hell, serious people who maintain it：
 * @Version:        更新说明: 1.0
┏┓　　　┏┓
┏┛┻━━━┛┻┓
┃　　　　　　　┃
┃　　　━　　　┃
┃　┳┛　┗┳　┃
┃　　　　　　　┃
┃　　　┻　　　┃
┃　　　　　　　┃
┗━┓　　　┏━┛
┃　　　┃   神兽保佑
┃　　　┃   代码无BUG！
┃　　　┗━━━┓
┃　　　　　　　┣┓
┃　　　　　　　┏┛
┗┓┓┏━┳┓┏┛
┃┫┫　┃┫┫
┗┻┛　┗┻┛
 */


data class Channel(
    val channelId: String,      // 唯一渠道ID
    val name: CharSequence,     // 用户可见名称
    val importance: Int,        // 重要性级别
    val description: String? = null,      // 描述
    @NotificationCompat.NotificationVisibility
    val lockScreenVisibility: Int = NotificationCompat.VISIBILITY_SECRET,        // 锁定屏幕公开范围
    val vibrate: LongArray? = null,      // 震动模式
    val sound: Uri? = null               // 声音
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Channel

        if (channelId != other.channelId) return false
        if (name != other.name) return false
        if (importance != other.importance) return false
        if (description != other.description) return false
        if (lockScreenVisibility != other.lockScreenVisibility) return false
        if (vibrate != null) {
            if (other.vibrate == null) return false
            if (!vibrate.contentEquals(other.vibrate)) return false
        } else if (other.vibrate != null) return false
        if (sound != other.sound) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channelId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + importance
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + lockScreenVisibility
        result = 31 * result + (vibrate?.contentHashCode() ?: 0)
        result = 31 * result + (sound?.hashCode() ?: 0)
        return result
    }
}
















