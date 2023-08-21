package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class SelfDestruct extends Module {

    public SelfDestruct() {
        super("Self Destruct", ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        Vestige.instance.getModuleManager().modules.forEach(m -> m.setEnabled(false));
        Vestige.instance.getPacketDelayHandler().stopAll();
        Vestige.instance.getPacketBlinkHandler().stopAll();
        Vestige.instance.getCameraHandler().setFreelooking(false);
        Vestige.instance.getSlotSpoofHandler().stopSpoofing();

        mc.displayGuiScreen(null);

        mc.timer.timerSpeed = 1F;

        Vestige.instance.setDestructed(true);
    }

}