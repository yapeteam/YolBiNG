package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Safewalk extends Module {
    private static Safewalk safewalk;
    public static Safewalk getInstance(){
        return safewalk;
    }

    public final BooleanValue offGround = new BooleanValue("Offground", false);

    public Safewalk() {
        super("Safewalk", ModuleCategory.MOVEMENT);
        this.addValues(offGround);
        safewalk=this;
    }

}
