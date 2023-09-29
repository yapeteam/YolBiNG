package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.RenderEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

@ModuleInfo(name = "Notification", category = ModuleCategory.VISUAL)
public class Notification extends Module {
    private final ArrayList<cn.yapeteam.yolbi.ui.noti.Notification> notifications = new ArrayList<>();

    @Listener
    private void onRender(RenderEvent e) {
        ScaledResolution sr = new ScaledResolution(mc);
        for (int i = 0; i < notifications.size(); i++) {
            cn.yapeteam.yolbi.ui.noti.Notification notification = notifications.get(i);
            notification.setTargetY(sr.getScaledHeight() - (cn.yapeteam.yolbi.ui.noti.Notification.getHeight() + 4) * (notifications.indexOf(notification) + 1));
            if (notification.getLeftTime() > 0) notification.render();
            else notifications.remove(notification);
            notification.setIndex(i);
        }
    }

    public void add(cn.yapeteam.yolbi.ui.noti.Notification notification) {
        ScaledResolution sr = new ScaledResolution(mc);
        notification.setTargetX(sr.getScaledWidth() - notification.getWidth() - 2);
        notification.setCurrentX(sr.getScaledWidth() + 2);
        notification.setCurrentY(sr.getScaledHeight() - (cn.yapeteam.yolbi.ui.noti.Notification.getHeight() + 4) * (notifications.size() + 1));
        notifications.add(notification);
    }
}
