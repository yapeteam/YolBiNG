package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.NumberValue;

@ModuleInfo(name = "Reach", category = ModuleCategory.COMBAT)
public class Reach extends Module {
    private static Reach Ireach;

    public static Reach getInstance() {
        return Ireach;
    }

    public final NumberValue<Double> startingReach = new NumberValue<>("Starting reach", 3.5, 3.0, 6.0, 0.05);
    public final NumberValue<Double> reach = new NumberValue<>("Reach", 3.5, 3.0, 6.0, 0.05);

    public Reach() {
        this.addValues(startingReach, reach);
        Ireach = this;
    }
}
