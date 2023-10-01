package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.util.render.animation.Animation;
import cn.yapeteam.yolbi.util.render.animation.Easing;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.awt.*;

@ModuleInfo(name = "AutoTool", category = ModuleCategory.PLAYER)
public class AutoTool extends Module {

    private int oldSlot;

    private boolean wasDigging;
    private int windowX,windowY=0;
    private ItemStack lastItem=null;
    private final Animation animation = new Animation(Easing.EASE_OUT_ELASTIC,3000);
    private final BooleanValue spoof = new BooleanValue("Item spoof", false);
    private final BooleanValue render = new BooleanValue("Render Item", true);


    public AutoTool() {
        this.addValues(spoof,render);
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
    public void onTick(EventTick event) {
        if ((Mouse.isButtonDown(0) || mc.gameSettings.keyBindAttack.isKeyDown()) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {


            if (!wasDigging) {
                oldSlot = mc.thePlayer.inventory.currentItem;
                if (spoof.getValue()) {
                    YolBi.instance.getSlotSpoofHandler().startSpoofing(oldSlot);
                }
            }
            getTool(true);



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

    @Listener
    protected void onRender(EventRender2D eventRender2D){
        if (!render.getValue()) return;
        ItemStack renderItem = getTool(false);
//        if (renderItem != null) {
//            lastItem
//        }
        windowX = eventRender2D.getScaledresolution().getScaledWidth()/2-5;
        windowY = eventRender2D.getScaledresolution().getScaledHeight();
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && renderItem!=null){
            animation.run(windowY/2+50);
            RenderUtil.drawBloomShadow(windowX,(int)animation.getValue(),15f,15f,10,10,
                    new Color(23, 23, 23, 166));
            RenderUtil.renderItemIcon(windowX,(int)animation.getValue(),renderItem);
            lastItem = renderItem;
        }else if (lastItem != null){
            animation.run(windowY);
            RenderUtil.drawBloomShadow(windowX,(int)animation.getValue(),15f,15f,10,10,
                    new Color(60, 60, 60, 82));
            RenderUtil.renderItemIcon(windowX,(int)animation.getValue(),lastItem);
        }

    }
    private ItemStack getTool(Boolean isSet){
        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return null;
        final Block block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
        if (block==null) return null;
        float strength = 0;
        ItemStack stack;
        ItemStack bestStack = mc.thePlayer.inventory.getStackInSlot(0);
        for (int i = 0; i <= 8; i++) {
            stack = mc.thePlayer.inventory.getStackInSlot(i);

            if (stack != null) {
                float slotStrength = stack.getStrVsBlock(block);

                if (slotStrength > strength) {
                    if (isSet) mc.thePlayer.inventory.currentItem = i;
                    bestStack = stack;
                    strength = slotStrength;
                }
            }
        }
        return bestStack;
    }
}
