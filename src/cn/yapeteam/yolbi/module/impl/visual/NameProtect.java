package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;

@ModuleInfo(name = "NameProtect", category = ModuleCategory.VISUAL)
public class NameProtect extends Module {
    private static NameProtect nameProtect;

    public static NameProtect getInstance() {
        return nameProtect;
    }

    public NameProtect() {
        nameProtect = this;
    }
}
