package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.module.impl.visual
 * don't mind
 * @date 2023/8/23 17:19
 */
@ModuleInfo(name = "ItemPhysic", category = ModuleCategory.VISUAL)
public class ItemPhysic extends Module {
    private static ItemPhysic itemPhysic;

    public static ItemPhysic getInstance() {
        return itemPhysic;
    }

    public ItemPhysic() {
        itemPhysic = this;
    }

}
