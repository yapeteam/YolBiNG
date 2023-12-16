package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.ModeValue;

/**
 * @author TIMER_err
 * @see cn.yapeteam.yolbi.ui.noti.NotificationManager
 */
@ModuleInfo(name = "Notifications", category = ModuleCategory.VISUAL)
public class Notifications extends Module {
    public final ModeValue mode = new ModeValue<>("mode","Yolbi","Yolbi","Tag");

    public Notifications() {
        this.addValue(mode);
    }

    @Override
    protected void onEnable() {
        YolBi.instance.getNotificationManager().clear();
    }

    @Override
    protected void onDisable() {
        YolBi.instance.getNotificationManager().clear();
    }
}
