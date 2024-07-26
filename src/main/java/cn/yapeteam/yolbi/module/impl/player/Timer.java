package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.player.EventPostMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.ui.noti.Notification;
import cn.yapeteam.yolbi.ui.noti.NotificationManager;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;

@ModuleInfo(name = "Timer", category = ModuleCategory.PLAYER)
public class Timer extends Module {
    private final NumberValue<Double> speed = new NumberValue<>("Speed", 1.1, 0.1, 5.0, 0.1);

    private final BooleanValue balancedBoost = new BooleanValue("balancedBoost", false);
    private final NumberValue<Integer> waitTick = new NumberValue<>("WaitTick", balancedBoost::getValue,10, 0, 500, 1);
    private final NumberValue<Double> waitingSpeed = new NumberValue<>("WaitingSpeed",balancedBoost::getValue,0.2,0.1,1.0,0.02);
    private final NumberValue<Integer> boostTick = new NumberValue<>("BoostTick", balancedBoost::getValue,10, 0, 500, 1);
    private final NumberValue<Integer> autoDisable = new NumberValue<>("AutoDisable", balancedBoost::getValue,1, 0, 10, 1);

    private int countWaitTick = 0;
    private int countBoostTick = 0;
    private int countAutoDisable = 0;

    private boolean onSlow = false;


    public Timer() {
        this.addValues(speed,balancedBoost,waitTick,boostTick,waitingSpeed,autoDisable);
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        countWaitTick = 0;
        countBoostTick = 0;
        onSlow = false;
        countAutoDisable = 0;
    }

    @Override
    protected void onEnable() {
        if (balancedBoost.getValue()) onSlow = true;
    }

    @Listener
    public void onPostMotion(EventPostMotion event) {
        if (balancedBoost.getValue()){
            if (onSlow) mc.timer.timerSpeed = waitingSpeed.getValue().floatValue();
            else mc.timer.timerSpeed = speed.getValue().floatValue();

        }else mc.timer.timerSpeed = speed.getValue().floatValue();


    }

    @Listener
    public void onTick(EventTick event) {
        if (balancedBoost.getValue()){

            if (autoDisable.getValue() != 0 && countAutoDisable >= autoDisable.getValue()) {
                mc.timer.timerSpeed = 1f;
                this.toggle();
            }

            if (onSlow){
                countWaitTick++;
                if (countWaitTick >= waitTick.getValue()) {
                    countWaitTick = 0;
                    onSlow = false;
                }
            }else {
                countBoostTick++;
                if (countBoostTick >= boostTick.getValue()) {
                    countBoostTick = 0;
                    onSlow = true;
                    countAutoDisable++;
                    YolBi.instance.getNotificationManager().add(new Notification("开始加速"));
                }
            }


        }
    }


}
