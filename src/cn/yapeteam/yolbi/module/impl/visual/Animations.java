package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;

@ModuleInfo(name = "Animations", category = ModuleCategory.VISUAL)
public class Animations extends Module {
    private static Animations animations;

    public static Animations getInstance() {
        return animations;
    }

    public final NumberValue<Double> swingSlowdown = new NumberValue<>("Swing slowdown", 1.0, 0.1, 8.0, 0.1);
    public final ModeValue<String> modeValue = new ModeValue<>("Mode","Stella","Stella", "Middle", "1.7", "Exhi", "Exhi 2", "Exhi 3", "Exhi 4", "Exhi 5", "Shred", "Smooth", "Sigma");

    public final BooleanValue oldDamage = new BooleanValue("Old Damage", false);
    //public final BooleanValue smallSwing = new BooleanValue("Small Swing", false);
    public final NumberValue<Integer> xpos = new NumberValue<>("X", 0, -50, 50, 1);
    public final NumberValue<Integer> ypos = new NumberValue<>("Y", 0, -50, 50, 1);
    public final NumberValue<Integer> size = new NumberValue<>("Size", 0, -50, 50, 1);
    public Animations() {
        this.addValues(modeValue,swingSlowdown,oldDamage,xpos,ypos,size);
        animations = this;
    }
}
