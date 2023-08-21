package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.event.impl.PostMotionEvent;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Timer extends Module {

    private final NumberValue<Double> speed = new NumberValue<>("Speed", 1.1, 0.1, 5.0, 0.1);

    public Timer() {
        super("Timer", ModuleCategory.PLAYER);
        this.addValues(speed);
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
    }

    @Listener
    public void onPostMotion(PostMotionEvent event) {
        mc.timer.timerSpeed = speed.getValue().floatValue();
    }

}
