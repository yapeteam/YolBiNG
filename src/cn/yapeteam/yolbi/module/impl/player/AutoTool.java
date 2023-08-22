package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.game.TickEvent;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class AutoTool extends Module {

    private int oldSlot;

    private boolean wasDigging;

    private final BooleanValue spoof = new BooleanValue("Item spoof", false);

    public AutoTool() {
        super("AutoTool", ModuleCategory.PLAYER);
        this.addValues(spoof);
    }

    @Override
    public void onDisable() {
        if (wasDigging) {
            mc.thePlayer.inventory.currentItem = oldSlot;
            wasDigging = false;
        }

        YolBi.instance.getSlotSpoofHandler().stopSpoofing();
    }

    @Listener(Priority.LOW)
    public void onTick(TickEvent event) {
        if ((Mouse.isButtonDown(0) || mc.gameSettings.keyBindAttack.isKeyDown()) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

            float strength = 0;

            if (!wasDigging) {
                oldSlot = mc.thePlayer.inventory.currentItem;

                if (spoof.getValue()) {
                    YolBi.instance.getSlotSpoofHandler().startSpoofing(oldSlot);
                }
            }

            for (int i = 0; i <= 8; i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                if (stack != null) {
                    float slotStrength = stack.getStrVsBlock(block);

                    if (slotStrength > strength) {
                        mc.thePlayer.inventory.currentItem = i;
                        strength = slotStrength;
                    }
                }
            }

            wasDigging = true;
        } else {
            if (wasDigging) {
                mc.thePlayer.inventory.currentItem = oldSlot;

                YolBi.instance.getSlotSpoofHandler().stopSpoofing();

                wasDigging = false;
            } else {
                oldSlot = mc.thePlayer.inventory.currentItem;
            }
        }
    }

}
