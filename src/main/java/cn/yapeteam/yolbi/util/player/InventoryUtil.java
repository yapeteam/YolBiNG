package cn.yapeteam.yolbi.util.player;

import cn.yapeteam.yolbi.util.IMinecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.util.player
 * don't mind
 * @date 2023/8/23 10:54
 */
public class InventoryUtil implements IMinecraft {
    public static int findItem(int startSlot, int endSlot, Item item){
        for (int i=startSlot;i<=endSlot;i++){
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static int getBowAtHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof ItemBow)) continue;
            return i;
        }
        return -1;
    }
}
