package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class DelayRemover extends Module {
    private static DelayRemover delayRemover;
    public static DelayRemover getInstance(){
        return delayRemover;
    }

    public DelayRemover() {
        super("Delay Remover", ModuleCategory.COMBAT);
        delayRemover=this;
    }

}
