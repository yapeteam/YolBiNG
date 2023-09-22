package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.anticheat.ACPlayer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.misc.LogUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(name = "AntiBot", category = ModuleCategory.COMBAT)
public class AntiBot extends Module {
    private final NumberValue<Integer> ticksExisted = new NumberValue<>("Ticks existed", 30, 0, 100, 5);
    public final BooleanValue advancedDetection = new BooleanValue("Advanced detection", true);

    public final BooleanValue debug = new BooleanValue("Debug", false);

    private KillAura killauraModule;

    public AntiBot() {
        this.addValues(ticksExisted, advancedDetection, debug);
    }

    @Override
    public void onClientStarted() {
        killauraModule = YolBi.instance.getModuleManager().getModule(KillAura.class);
    }

    public boolean canAttack(EntityLivingBase entity, Module module) {
        if (!this.isEnabled()) return true;

        if (entity.ticksExisted < ticksExisted.getValue()) {
            if (debug.getValue() && module == killauraModule) {
                LogUtil.addChatMessage("Ticks existed antibot : prevented from hitting : " + entity.ticksExisted);
            }

            return false;
        }

        if (entity instanceof EntityPlayer) {
            ACPlayer player = YolBi.instance.getAnticheat().getACPlayer((EntityPlayer) entity);

            if (player != null && player.isBot()) {
                if (debug.getValue() && module == killauraModule) {
                    LogUtil.addChatMessage("Advanced antibot : prevented from hitting");
                }
                return false;
            }
        }

        return true;
    }
}