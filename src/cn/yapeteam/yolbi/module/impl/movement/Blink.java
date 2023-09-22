package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.PacketReceiveEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.player.PendingVelocity;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(name = "Blink", category = ModuleCategory.MOVEMENT)
public class Blink extends Module {

    private final BooleanValue suspendPositionOnly = new BooleanValue("Suspend position only", false);
    private final BooleanValue stackVelocity = new BooleanValue("Stack velocity", true);

    private PendingVelocity lastVelocity;

    public Blink() {
        this.addValues(suspendPositionOnly, stackVelocity);
    }

    @Override
    public void onEnable() {
        lastVelocity = null;

        if (suspendPositionOnly.getValue()) {
            YolBi.instance.getPacketBlinkHandler().startBlinkingMove();
        } else {
            YolBi.instance.getPacketBlinkHandler().startBlinkingAll();
        }
    }

    @Override
    public void onDisable() {
        YolBi.instance.getPacketBlinkHandler().stopAll();

        if (stackVelocity.getValue() && lastVelocity != null) {
            mc.thePlayer.motionX = lastVelocity.getX();
            mc.thePlayer.motionY = lastVelocity.getY();
            mc.thePlayer.motionZ = lastVelocity.getZ();
        }
    }

    @Listener
    public void onReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = event.getPacket();

            if (packet.getEntityID() == mc.thePlayer.getEntityId() && stackVelocity.getValue()) {
                event.setCancelled(true);
                lastVelocity = new PendingVelocity(packet.getMotionX() / 8000.0, packet.getMotionY() / 8000.0, packet.getMotionZ() / 8000.0);
            }
        }
    }
}
