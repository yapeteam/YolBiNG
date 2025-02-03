package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventPostMotion;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.player.PlayerUtil;
import cn.yapeteam.yolbi.values.impl.NumberValue;

@ModuleInfo(name = "TickBase", category = ModuleCategory.COMBAT)
public class TickBase extends Module {
    private static TickBase tickbase;

    public static TickBase getInstance() {
        return tickbase;
    }

    private KillAura killauraModule;

    private int counter = -1;
    public boolean freezing;

    private final NumberValue<Integer> ticks = new NumberValue<>("Ticks", 3, 1, 10, 1);

    public TickBase() {
        this.addValues(ticks);
        tickbase = this;
    }

    @Override
    public void onEnable() {
        counter = -1;
        freezing = false;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onClientStarted() {
        killauraModule = YolBi.instance.getModuleManager().getModule(KillAura.class);
    }

    public int getExtraTicks() {
        if (counter-- > 0) {
            return -1;
        } else {
            freezing = false;
        }

        if (killauraModule.isEnabled() && (killauraModule.getTarget() == null || PlayerUtil.getDistanceToEntity(killauraModule.getTarget()) > killauraModule.range.getValue())) {
            if (killauraModule.findTarget(!killauraModule.mode.is("Fast Switch"), killauraModule.startingRange.getValue() + 0.75) != null && mc.thePlayer.hurtTime <= 2) {
                return counter = ticks.getValue();
            }
        }

        return 0;
    }

    @Listener
    public void onPostMotion(EventPostMotion event) {
        if (freezing) {
            mc.thePlayer.posX = mc.thePlayer.lastTickPosX;
            mc.thePlayer.posY = mc.thePlayer.lastTickPosY;
            mc.thePlayer.posZ = mc.thePlayer.lastTickPosZ;
        }
    }

    @Listener
    public void onRender(EventRender2D event) {
        if (freezing) {
            mc.timer.renderPartialTicks = 0F;
        }
    }

}
