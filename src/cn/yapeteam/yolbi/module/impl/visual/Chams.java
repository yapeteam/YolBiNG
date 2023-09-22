package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;

@ModuleInfo(name = "Chams", category = ModuleCategory.VISUAL)
public class Chams extends Module {
    private static Chams chams;

    public static Chams getInstance() {
        return chams;
    }

    public Chams() {
        chams = this;
    }
}
