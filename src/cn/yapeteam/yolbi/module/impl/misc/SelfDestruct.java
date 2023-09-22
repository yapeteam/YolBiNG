package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;

@ModuleInfo(name = "Self Destruct", category = ModuleCategory.MISC)
public class SelfDestruct extends Module {
    @Override
    public void onEnable() {
        YolBi.instance.getModuleManager().modules.forEach(m -> m.setEnabled(false));
        YolBi.instance.getPacketDelayHandler().stopAll();
        YolBi.instance.getPacketBlinkHandler().stopAll();
        YolBi.instance.getCameraHandler().setFreelooking(false);
        YolBi.instance.getSlotSpoofHandler().stopSpoofing();

        mc.displayGuiScreen(null);

        mc.timer.timerSpeed = 1F;

        YolBi.instance.setDestructed(true);
    }
}