package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.UpdateEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;

@ModuleInfo(name = "FullBright", category = ModuleCategory.VISUAL)
public class FullBright extends Module {
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
