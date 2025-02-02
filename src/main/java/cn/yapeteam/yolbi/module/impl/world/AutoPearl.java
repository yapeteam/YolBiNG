package cn.yapeteam.yolbi.module.impl.world;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import cn.yapeteam.yolbi.values.impl.NumberValue;

/**
 * @author yuxiangll
 * @since 2024/9/17 下午3:22
 * IntelliJ IDEA
 */
@ModuleInfo(name = "AutoPearl", category = ModuleCategory.WORLD)
public class AutoPearl extends Module {

    private final NumberValue<Double> fallDistance = new NumberValue<>("Fall Distance",3.0,1.0,10.0,0.1);

    public AutoPearl(){
        this.addValue(fallDistance);
    }


    @Listener
    public void onTick(EventTick eventTick){
        if (mc.thePlayer.ticksExisted < 10) return;

        if (isFallingToVoid()){


        }

    }


    private boolean isFallingToVoid(){
        return mc.thePlayer.fallDistance >= fallDistance.getValue() && !WorldUtil.isBlockUnder() && mc.thePlayer.ticksExisted >= 100;

    }

}
