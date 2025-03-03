package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventPostStep;
import cn.yapeteam.yolbi.event.impl.player.EventPreStep;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(name = "Step", category = ModuleCategory.MOVEMENT)
public class Step extends Module {

    private final ModeValue<String> mode = new ModeValue<>("Mode", "Vanilla", "Vanilla", "NCP");
    private final NumberValue<Double> height = new NumberValue<>("Height", () -> mode.is("Vanilla"), 1.0, 1.0, 9.0, 0.5);

    private final NumberValue<Double> timer = new NumberValue<>("Timer", 1.0, 0.1, 1.0, 0.05);

    private boolean prevOffGround;

    private boolean timerTick;

    public Step() {
        this.addValues(mode, height, timer);
    }

    @Override
    public void onDisable() {
        prevOffGround = false;

        mc.timer.timerSpeed = 1F;
        mc.thePlayer.stepHeight = 0.6F;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        switch (mode.getValue()) {
            case "Vanilla":
                mc.thePlayer.stepHeight = height.getValue().floatValue();
                break;
            case "NCP":
                mc.thePlayer.stepHeight = 1F;
                break;
        }

        if (timerTick) {
            mc.timer.timerSpeed = 1F;
            timerTick = false;
        }
    }

    @Listener
    public void onPreStep(EventPreStep event) {
        if (!mode.is("Vanilla")) {
            if (mc.thePlayer.onGround && prevOffGround) {
                if (event.getHeight() > 0.6) {
                    event.setHeight(0.6F);
                }
            }
        }
    }

    @Listener
    public void onPostStep(EventPostStep event) {
        if (event.getHeight() > 0.6F) {
            if (timer.getValue() < 1) {
                mc.timer.timerSpeed = timer.getValue().floatValue();
                timerTick = true;
            }

            if (mode.getValue().equals("NCP")) {
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ, false));
                PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.75, mc.thePlayer.posZ, false));
            }
        }
    }

    @Listener
    public void onMotion(EventMotion event) {
        prevOffGround = !event.isOnGround();
    }

}
