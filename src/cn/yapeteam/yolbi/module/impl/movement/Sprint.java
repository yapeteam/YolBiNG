package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.event.impl.game.TickEvent;
import cn.yapeteam.yolbi.event.impl.player.StrafeEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.network.ServerUtil;
import cn.yapeteam.yolbi.values.impl.ModeValue;

import java.util.Objects;

@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {
    public final ModeValue<String> mode = new ModeValue<>("Mode", "Simple", "Simpe", "Legit");


    public Sprint() {
        this.setEnabledSilently(true);
        this.addValues(mode);
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.setPressed(false);
    }

    @Listener
    public void onStrafe(StrafeEvent event) {
        if (Objects.equals(mode.getValue(), "Legit")) mc.gameSettings.keyBindSprint.setPressed(true);
    }

    @Listener(Priority.HIGH)
    public void onTick(TickEvent event) {
        if (mode.is("Simple")) {
            mc.gameSettings.keyBindSprint.setPressed(true);

            // It's needed on hypixel for some reason with spoof autoblock only (otherwise you can sprint sideaways for some reason), if you find the issue please let me know.
            // It doesn't do that on other servers btw
            if (mc.thePlayer.moveForward <= 0F && ServerUtil.isOnHypixel()) {
                mc.thePlayer.setSprinting(false); // Sprint ticks are over 0 even if mc.thePlayer.isSprinting() returns false
            }
        }
    }
}