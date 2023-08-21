package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.event.impl.RenderEvent;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Autoplace extends Module {

    private final BooleanValue placeUnderWhileOffground = new BooleanValue("Place under while offground", false);

    public Autoplace() {
        super("Autoplace", ModuleCategory.PLAYER);
        this.addValues(placeUnderWhileOffground);
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
    }

    @Listener(Priority.HIGH)
    public void onRender(RenderEvent event) {
        if(mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            EnumFacing facing = mc.objectMouseOver.sideHit;

            boolean canPlaceOffGround = placeUnderWhileOffground.getValue() && !mc.thePlayer.onGround && WorldUtil.isAirOrLiquid(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));

            if((facing != EnumFacing.UP && facing != EnumFacing.DOWN) || canPlaceOffGround) {
                mc.gameSettings.keyBindUseItem.pressed = true;
                mc.rightClickDelayTimer = 0;
            } else {
                mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            }
        }
    }
}
