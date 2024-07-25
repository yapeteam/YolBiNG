package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;

@ModuleInfo(name = "DelayRemover", category = ModuleCategory.COMBAT)
public class DelayRemover extends Module {
    private static DelayRemover delayRemover;

    public static DelayRemover getInstance() {
        return delayRemover;
    }

    public DelayRemover() {
        delayRemover = this;
    }
}
