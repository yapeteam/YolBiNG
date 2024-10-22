package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "FastPlace", category = ModuleCategory.PLAYER)
public class FastPlace extends Module {
    private final ModeValue<String> delay = new ModeValue<>("Delay", "Every tick", "Every tick", "Every 2 ticks", "No CPS cap", "Every tick on render");

    private C08PacketPlayerBlockPlacement placement;

    private boolean placedBlock;

    public FastPlace() {
        this.addValues(delay);
    }

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 6;
        placement = null;
    }

    @Listener
    public void onTick(EventTick event) {
        if (delay.is("Every tick")) {
            mc.rightClickDelayTimer = 0;
        } else if (delay.is("Every 2 ticks")) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                mc.rightClickDelayTimer = 0;
            }
        }

        if (delay.is("Every tick on render")) {
            if (placement != null) {
                PacketUtil.sendPacketFinal(placement);
                placement = null;
            }
        }

        placedBlock = false;
    }

    @Listener
    public void onRender(EventRender2D event) {
        if (delay.is("No CPS cap") || (delay.is("Every tick on render") && !placedBlock)) {
            if (mc.gameSettings.keyBindUseItem.isPressed() || Mouse.isButtonDown(1)) {
                mc.rightClickDelayTimer = 0;
                mc.rightClickMouse();
            }
        }
    }

    @Listener
    public void onSend(EventPacketSend event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement packet = event.getPacket();

            if (delay.is("No CPS cap") || delay.is("Every tick on render")) {
                if (packet.getPosition().equals(new BlockPos(-1, -1, -1))) {
                    event.setCancelled(true);
                } else {
                    placedBlock = true;

                    if (delay.is("Every tick on render")) {
                        event.setCancelled(true);
                        placement = packet;
                    }
                }
            }
        }
    }

}
