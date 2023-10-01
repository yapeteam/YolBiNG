package cn.yapeteam.yolbi.ui.noti;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.impl.visual.Notifications;
import cn.yapeteam.yolbi.util.IMinecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

/**
 * @author TIMER_err
 */
public class NotificationManager implements IMinecraft {
    private final ArrayList<Notification> notifications = new ArrayList<>();
    private final Module notificationModule;

    public NotificationManager() {
        YolBi.instance.getEventManager().register(this);
        notificationModule = YolBi.instance.getModuleManager().getModule(Notifications.class);
    }

    @Listener
    private void onRender(EventRender2D e) {
        if (!notificationModule.isEnabled()) return;
        ScaledResolution sr = new ScaledResolution(mc);
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            notification.setTargetY(sr.getScaledHeight() - (Notification.getHeight() + 4) * (notifications.indexOf(notification) + 1));
            if (notification.getLeftTime() > 0) notification.render();
            else notifications.remove(notification);
        }
    }

    public void add(Notification notification) {
        ScaledResolution sr = new ScaledResolution(mc);
        notification.setTargetX(sr.getScaledWidth() - notification.getWidth() - 2);
        notification.setCurrentX(sr.getScaledWidth() + 2);
        notification.setCurrentY(sr.getScaledHeight() - (Notification.getHeight() + 4) * (notifications.size() + 1));
        notification.setIndex(notifications.size());
        notifications.add(notification);
    }

    public void clear() {
        notifications.clear();
    }
}
