package cn.yapeteam.yolbi.packetfix;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.impl.misc.PacketFix;

public class FixEngine {
    /**
     * Fix for right-clicking
     * Made by vlouboos
     */
    public static float fixRightClick() {
        if (YolBi.instance.getModuleManager().getModule(PacketFix.class).isEnabled()) {
            return 16.0F;
        } else {
            return 1.0F;
        }
    }
}
