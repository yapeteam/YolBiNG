package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.player.RotationsUtil;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;

@ModuleInfo(name = "TargetStrafe", category = ModuleCategory.COMBAT)
public class TargetStrafe extends Module {
    private final NumberValue<Double> maxRange = new NumberValue<>("Max range", 3.0, 1.0, 6.0, 0.1);
    public final BooleanValue whilePressingSpace = new BooleanValue("While pressing space", false);

    private boolean goingRight;

    private KillAura killaura;

    public TargetStrafe() {
        this.addValues(maxRange, whilePressingSpace);
    }

    @Override
    public void onClientStarted() {
        killaura = YolBi.instance.getModuleManager().getModule(KillAura.class);
    }

    public float getDirection() {
        if (mc.thePlayer.isCollidedHorizontally || !WorldUtil.isBlockUnder(3)) {
            goingRight = !goingRight;
        }

        EntityLivingBase target = killaura.getTarget();

        double distance = killaura.getDistanceToEntity(target);

        float direction;

        if (distance > maxRange.getValue()) {
            direction = RotationsUtil.getRotationsToEntity(target, false)[0];
        } else {
            double offset = (90 - killaura.getDistanceToEntity(target) * 5);

            if (!goingRight) {
                offset = -offset;
            }

            direction = (float) (RotationsUtil.getRotationsToEntity(target, false)[0] + offset);
        }

        return (float) Math.toRadians(direction);
    }

}
