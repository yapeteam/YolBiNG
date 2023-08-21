package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.impl.UpdateEvent;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Fullbright extends Module {

    public Fullbright() {
        super("Fullbright", ModuleCategory.VISUAL);
    }

    public void onEnable() {
        mc.gameSettings.gammaSetting = 100F;
    }

    public void onDisable() {
        mc.gameSettings.gammaSetting = 1F;
    }

    @Listener
    public void onUpdate(UpdateEvent event) {
        mc.gameSettings.gammaSetting = 100F;
    }

}
