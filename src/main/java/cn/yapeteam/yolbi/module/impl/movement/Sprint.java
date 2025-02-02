package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.network.ServerUtil;
import cn.yapeteam.yolbi.values.impl.ModeValue;

import java.util.Objects;

@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {
    public final ModeValue<String> mode = new ModeValue<>("Mode", "Simple", "Simple");


    public Sprint() {
        this.addValues(mode);
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.setPressed(false);
    }


    @Listener
    public void onTick(EventTick event) {
        if (mode.is("Simple")) {
            mc.gameSettings.keyBindSprint.setPressed(true);

        }
    }
}