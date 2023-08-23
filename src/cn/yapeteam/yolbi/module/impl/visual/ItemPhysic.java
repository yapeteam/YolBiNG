package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.module.impl.visual
 * don't mind
 * @date 2023/8/23 17:19
 */
public class ItemPhysic extends Module {
    private static ItemPhysic itemPhysic;
    public static ItemPhysic getInstance(){
        return itemPhysic;
    }
    public ItemPhysic() {
        super("ItemPhysic",ModuleCategory.VISUAL);
        itemPhysic = this;
    }

}
