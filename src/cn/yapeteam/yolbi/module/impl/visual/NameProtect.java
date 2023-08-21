package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;

public class NameProtect extends Module {
    private static NameProtect nameProtect;
    public static NameProtect getInstance(){
        return nameProtect;
    }

    public NameProtect() {
        super("NameProtect", ModuleCategory.VISUAL);
        nameProtect=this;
    }

}
