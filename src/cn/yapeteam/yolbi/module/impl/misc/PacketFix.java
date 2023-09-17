package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.PacketSendEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import net.minecraft.network.play.client.C0APacketAnimation;

public class PacketFix extends Module {
    public PacketFix() {
        super("PacketFix 1.12.2", ModuleCategory.MISC);
    }

    @Listener
    private void onPacketSend(PacketSendEvent e) {
        if (e.getPacket() instanceof C0APacketAnimation)
            e.setCancelled(true);
    }
}
