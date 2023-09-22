package cn.yapeteam.yolbi.module.impl.world;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.TickEvent;
import cn.yapeteam.yolbi.event.impl.player.JumpEvent;
import cn.yapeteam.yolbi.event.impl.player.MotionEvent;
import cn.yapeteam.yolbi.event.impl.player.StrafeEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.player.FixedRotations;
import cn.yapeteam.yolbi.util.player.RotationsUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "Breaker", category = ModuleCategory.WORLD)
public class Breaker extends Module {

    private BlockPos bedPos;

    private FixedRotations rotations;

    private final NumberValue<Integer> range = new NumberValue<>("Range", 4, 1, 6, 1);

    private final BooleanValue rotate = new BooleanValue("Rotate", true);
    private final BooleanValue moveFix = new BooleanValue("Move fix", rotate::getValue, false);

    private final BooleanValue hypixel = new BooleanValue("Hypixel", false);

    public Breaker() {
        this.addValues(range, rotate, moveFix, hypixel);
    }

    @Override
    public void onEnable() {
        bedPos = null;

        rotations = new FixedRotations(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindAttack.setPressed(Mouse.isButtonDown(0));
    }

    @Listener
    public void onTick(TickEvent event) {
        bedPos = null;

        boolean found = false;

        if (rotations == null) return;

        float yaw = rotations.getYaw();
        float pitch = rotations.getPitch();

        for (double x = mc.thePlayer.posX - range.getValue(); x <= mc.thePlayer.posX + range.getValue(); x++) {
            for (double y = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - range.getValue(); y <= mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + range.getValue(); y++) {
                for (double z = mc.thePlayer.posZ - range.getValue(); z <= mc.thePlayer.posZ + range.getValue(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockBed && !found) {
                        bedPos = pos;

                        if (hypixel.getValue() && isBlockOver(bedPos)) {
                            BlockPos posOver = pos.add(0, 1, 0);

                            mc.objectMouseOver = new MovingObjectPosition(new Vec3(posOver.getX() + 0.5, posOver.getY() + 1, posOver.getZ() + 0.5), EnumFacing.UP, posOver);

                            mc.gameSettings.keyBindAttack.setPressed(true);

                            float[] rots = RotationsUtil.getRotationsToPosition(posOver.getX() + 0.5, posOver.getY() + 1, posOver.getZ() + 0.5);

                            yaw = rots[0];
                            pitch = rots[1];
                        } else {
                            mc.objectMouseOver = new MovingObjectPosition(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), EnumFacing.UP, bedPos);

                            mc.gameSettings.keyBindAttack.setPressed(true);

                            float[] rots = RotationsUtil.getRotationsToPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                            yaw = rots[0];
                            pitch = rots[1];
                        }

                        found = true;
                    }
                }
            }
        }

        if (!found) {
            mc.gameSettings.keyBindAttack.setPressed(Mouse.isButtonDown(0));
        }

        rotations.updateRotations(yaw, pitch);
    }

    public boolean isBlockOver(BlockPos pos) {
        BlockPos posOver = pos.add(0, 1, 0);

        Block block = mc.theWorld.getBlockState(posOver).getBlock();

        return !(block instanceof BlockAir || block instanceof BlockLiquid);
    }

    public boolean isBreakingBed() {
        return bedPos != null;
    }

    @Listener
    public void onStrafe(StrafeEvent event) {
        if (bedPos != null && rotate.getValue() && moveFix.getValue()) {
            event.setYaw(rotations.getYaw());
        }
    }

    @Listener
    public void onJump(JumpEvent event) {
        if (bedPos != null && rotate.getValue() && moveFix.getValue()) {
            event.setYaw(rotations.getYaw());
        }
    }

    @Listener
    public void onMotion(MotionEvent event) {
        if (bedPos != null && rotate.getValue()) {
            event.setYaw(rotations.getYaw());
            event.setPitch(rotations.getPitch());
        }
    }

}
