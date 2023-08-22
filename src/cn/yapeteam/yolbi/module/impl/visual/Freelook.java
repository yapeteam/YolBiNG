package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.game.TickEvent;
import org.lwjgl.input.Keyboard;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.EventListenType;
import cn.yapeteam.yolbi.module.Module;

public class Freelook extends Module {

    private boolean wasFreelooking;

    public Freelook() {
        super("Freelook", ModuleCategory.VISUAL);

        this.listenType = EventListenType.MANUAL;
        this.startListening();
    }

    @Listener
    public void onTick(TickEvent event) {
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
