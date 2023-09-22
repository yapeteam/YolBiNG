package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.UpdateEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "ChestStealer", category = ModuleCategory.PLAYER)
public class ChestStealer extends Module {

    private final NumberValue<Integer> delay = new NumberValue<>("Delay", 1, 0, 10, 1);
    private final BooleanValue filter = new BooleanValue("Filter", true);
    private final BooleanValue autoClose = new BooleanValue("Autoclose", true);
    private final BooleanValue guiDetect = new BooleanValue("Gui detect", true);

    private int counter;

    private InventoryManager invManager;

    public ChestStealer() {
        this.addValues(delay, filter, autoClose, guiDetect);
    }

    @Override
    public void onClientStarted() {
        invManager = YolBi.instance.getModuleManager().getModule(InventoryManager.class);
    }

    @Listener
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest && (!isGUI() || !guiDetect.getValue())) {
            ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;

            for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
                ItemStack stack = container.getLowerChestInventory().getStackInSlot(i);
                if (stack != null && !isUseless(stack)) {
                    if (++counter > delay.getValue()) {
                        mc.playerController.windowClick(container.windowId, i, 1, 1, mc.thePlayer);
                        counter = 0;
                        return;
                    }
                }
            }

            if (autoClose.getValue() && isChestEmpty(container)) {
                mc.thePlayer.closeScreen();
            }
        }
    }

    private boolean isChestEmpty(ContainerChest container) {
        for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
            ItemStack stack = container.getLowerChestInventory().getStackInSlot(i);

            if (stack != null && !isUseless(stack)) {
                return false;
            }
        }

        return true;
    }

    private boolean isUseless(ItemStack stack) {
        if (!filter.getValue()) {
            return false;
        }

        return invManager.isUseless(stack);
    }

    private boolean isGUI() {
        for (double x = mc.thePlayer.posX - 5; x <= mc.thePlayer.posX + 5; x++) {
            for (double y = mc.thePlayer.posY - 5; y <= mc.thePlayer.posY + 5; y++) {
                for (double z = mc.thePlayer.posZ - 5; z <= mc.thePlayer.posZ + 5; z++) {

                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = mc.theWorld.getBlockState(pos).getBlock();

                    if (block instanceof BlockChest || block instanceof BlockEnderChest) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
