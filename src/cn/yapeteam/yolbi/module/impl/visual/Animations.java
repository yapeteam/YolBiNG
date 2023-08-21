package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Animations extends Module {
    private static Animations animations;
    public static Animations getInstance(){
        return animations;
    }

    public final NumberValue<Double> swingSlowdown = new NumberValue<>("Swing slowdown", 1.0, 0.1, 8.0, 0.1);

    public Animations() {
        super("Animations", ModuleCategory.VISUAL);
        this.addValues(swingSlowdown);
        animations=this;
    }

}
