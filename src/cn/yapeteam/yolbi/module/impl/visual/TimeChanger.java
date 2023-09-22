package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.PacketReceiveEvent;
import cn.yapeteam.yolbi.event.impl.render.RenderEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@Deprecated
@ModuleInfo(name = "TimeChanger", category = ModuleCategory.VISUAL)
public class TimeChanger extends Module {

    private final NumberValue<Integer> customTime = new NumberValue<>("Custom time", 18000, 0, 24000, 500);

    public TimeChanger() {
        this.addValues(customTime);
    }

    @Listener
    public void onRender(RenderEvent event) {
        mc.theWorld.setWorldTime((long) customTime.getValue());
    }

    @Listener
    public void onReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    }
}
