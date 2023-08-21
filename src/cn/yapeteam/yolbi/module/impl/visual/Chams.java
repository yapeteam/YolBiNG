package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Chams extends Module {
    private static Chams chams;
    public static Chams getInstance(){
        return chams;
    }

    public Chams() {
        super("Chams", ModuleCategory.VISUAL);
        chams=this;
    }

}
