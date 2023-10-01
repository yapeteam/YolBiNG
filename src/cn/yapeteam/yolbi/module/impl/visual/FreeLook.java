package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.module.EventListenType;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "FreeLook", category = ModuleCategory.VISUAL)
public class FreeLook extends Module {

    private boolean wasFreelooking;

    public FreeLook() {
        this.listenType = EventListenType.MANUAL;
        this.startListening();
    }

    @Listener
    public void onTick(EventTick event) {
        if (mc.thePlayer.ticksExisted < 10) {
            stop();
        }

        if (Keyboard.isKeyDown(getKey())) {
            wasFreelooking = true;

            YolBi.instance.getCameraHandler().setFreelooking(true);

            mc.gameSettings.thirdPersonView = 1;
        } else {
            if (wasFreelooking) {
                stop();
            }
        }
    }

    private void stop() {
        this.setEnabled(false);

        YolBi.instance.getCameraHandler().setFreelooking(false);
        wasFreelooking = false;

        mc.gameSettings.thirdPersonView = 0;
    }
}
