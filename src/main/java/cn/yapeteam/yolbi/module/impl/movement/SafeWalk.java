package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.BooleanValue;

@ModuleInfo(name = "SafeWalk", category = ModuleCategory.MOVEMENT)
public class SafeWalk extends Module {
    private static SafeWalk safewalk;

    public static SafeWalk getInstance() {
        return safewalk;
    }

    public final BooleanValue offGround = new BooleanValue("OffGround", false);

    public SafeWalk() {
        this.addValues(offGround);
        safewalk = this;
    }

}
