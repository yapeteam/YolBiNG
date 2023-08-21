package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.impl.RenderEvent;
import cn.yapeteam.yolbi.event.impl.TickEvent;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.util.misc.TimerUtil;
import org.lwjgl.input.Mouse;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

import java.util.concurrent.ThreadLocalRandom;

public class Autoclicker extends Module {

    private boolean wasHoldingMouse;
    private boolean clickingTick;

    private final TimerUtil timer = new TimerUtil();

    private final NumberValue<Integer> minCPS = new NumberValue<>("Min CPS", 8, 1, 20, 1);
    private final NumberValue<Integer> maxCPS = new NumberValue<>("Max CPS", 15, 1, 20, 1);

    public Autoclicker() {
        super("Autoclicker", ModuleCategory.COMBAT);
        this.addValues(minCPS, maxCPS);
    }

    @Override
    public void onEnable() {
        wasHoldingMouse = false;
    }

    @Listener
    public void onRender(RenderEvent event) {
        if (wasHoldingMouse) {
            long maxDelay = (long) (1000.0 / minCPS.getValue());
            long minDelay = (long) (1000.0 / maxCPS.getValue());

            long delay = maxDelay > minDelay ? ThreadLocalRandom.current().nextLong(minDelay, maxDelay) : minDelay;

            if (timer.getTimeElapsed() >= delay) {
                clickingTick = true;
                timer.reset();
            }
        }
    }

    @Listener
    public void onTick(TickEvent event) {
        if (Mouse.isButtonDown(0)) {
            if (wasHoldingMouse && clickingTick) {
                mc.leftClickCounter = 0;
                mc.clickMouse();

                clickingTick = false;
            }

            wasHoldingMouse = true;
        } else {
            wasHoldingMouse = false;
        }
    }

}
