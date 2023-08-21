package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.impl.TickEvent;
import cn.yapeteam.yolbi.util.network.ServerUtil;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", ModuleCategory.MOVEMENT);
        this.setEnabledSilently(true);
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = false;
    }

    @Listener(Priority.HIGH)
    public void onTick(TickEvent event) {
        mc.gameSettings.keyBindSprint.pressed = true;

        // It's needed on hypixel for some reason with spoof autoblock only (otherwise you can sprint sideaways for some reason), if you find the issue please let me know.
        // It doesn't do that on other servers btw
        if (mc.thePlayer.moveForward <= 0F && ServerUtil.isOnHypixel()) {
            mc.thePlayer.setSprinting(false); // Sprint ticks are over 0 even if mc.thePlayer.isSprinting() returns false
        }
    }
}