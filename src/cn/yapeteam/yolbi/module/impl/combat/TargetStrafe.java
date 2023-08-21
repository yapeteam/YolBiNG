package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.util.player.RotationsUtil;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import net.minecraft.entity.EntityLivingBase;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class TargetStrafe extends Module {

    private final NumberValue maxRange = new NumberValue("Max range", 3, 1, 6, 0.1);
    public final BooleanValue whilePressingSpace = new BooleanValue("While pressing space", false);

    private boolean goingRight;

    private Killaura killaura;

    public TargetStrafe() {
        super("Target Strafe", ModuleCategory.COMBAT);
        this.addValues(maxRange, whilePressingSpace);
    }

    @Override
    public void onClientStarted() {
        killaura = Vestige.instance.getModuleManager().getModule(Killaura.class);
    }

    public float getDirection() {
        if(mc.thePlayer.isCollidedHorizontally || !WorldUtil.isBlockUnder(3)) {
            goingRight = !goingRight;
        }

        EntityLivingBase target = killaura.getTarget();

        double distance = killaura.getDistanceToEntity(target);

        float direction;

        if(distance > maxRange.getValue()) {
            direction = RotationsUtil.getRotationsToEntity(target, false)[0];
        } else {
            double offset = (90 - killaura.getDistanceToEntity(target) * 5);

            if(!goingRight) {
                offset = -offset;
            }

            direction = (float) (RotationsUtil.getRotationsToEntity(target, false)[0] + offset);
        }

        return (float) Math.toRadians(direction);
    }

}
