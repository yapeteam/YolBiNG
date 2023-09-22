package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.PacketReceiveEvent;
import cn.yapeteam.yolbi.event.impl.player.MotionEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import lombok.val;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Source: HYTC07全反开源.kt
 */
@ModuleInfo(name = "GrimVelocity", category = ModuleCategory.COMBAT)
public class GrimVelocity extends Module {
    private int S08 = 0;
    private boolean isVel = false;

    @Listener
    private void onUpdate(MotionEvent e) {
        if (isVel) {
            isVel = false;
            mc.getNetHandler().getNetworkManager().sendPacket(
                    new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            new BlockPos(mc.thePlayer),
                            EnumFacing.NORTH
                    )
            );
        }
    }

    @Listener
    private void onReceive(PacketReceiveEvent e) {
        val packet = e.getPacket();
        if (S08 > 0) {
            S08--;
            return;
        }
        if (packet instanceof S08PacketPlayerPosLook)
            S08 = 10;
        if (packet instanceof S12PacketEntityVelocity) {
            e.setCancelled(true);
            isVel = true;
        }
    }
}
