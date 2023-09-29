package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;

/**
 * @author TIMER_err
 * @see cn.yapeteam.yolbi.ui.noti.NotificationManager
 */
@ModuleInfo(name = "Notifications", category = ModuleCategory.VISUAL)
public class Notifications extends Module {
    @Override
    protected void onEnable() {
        YolBi.instance.getNotificationManager().clear();
    }

    @Override
    protected void onDisable() {
        YolBi.instance.getNotificationManager().clear();
    }
}
