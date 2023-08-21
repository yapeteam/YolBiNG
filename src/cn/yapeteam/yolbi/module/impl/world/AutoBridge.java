package cn.yapeteam.yolbi.module.impl.world;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.event.impl.PacketSendEvent;
import cn.yapeteam.yolbi.event.impl.RenderEvent;
import cn.yapeteam.yolbi.event.impl.TickEvent;
import cn.yapeteam.yolbi.event.impl.UpdateEvent;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.player.FixedRotations;
import cn.yapeteam.yolbi.util.player.KeyboardUtil;
import cn.yapeteam.yolbi.util.player.PlayerUtil;
import cn.yapeteam.yolbi.util.world.BlockInfo;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class AutoBridge extends Module {

    private final ModeValue<String> mode = new ModeValue<>("Mode", "Sprint", "Sprint", "No sprint", "Godbridge");

    private final BooleanValue keepY = new BooleanValue("Keep Y", () -> mode.is("Sprint"), true);

    private final BooleanValue ninjaBridge = new BooleanValue("Ninja bridge", () -> mode.is("No sprint"), false);
    private final BooleanValue eagle = new BooleanValue("Eagle", () -> mode.is("No sprint"), false);

    private final BooleanValue alwaysRotateOffground = new BooleanValue("Always rotate offground", () -> mode.is("Sprint"), false);

    private final BooleanValue freelook = new BooleanValue("Freelook", true);

    private final ModeValue<String> blockPicker = new ModeValue<>("Block picker", "Switch", "None", "Switch", "Spoof");

    private FixedRotations rotations;

    private float oldYaw, oldPitch;

    private boolean freelooking;

    private double lastGroundY;

    private boolean started;

    private int blocksPlaced;

    private int ticks, counter;

    private int oldSlot;

    public AutoBridge() {
        super("AutoBridge", ModuleCategory.WORLD);
        this.addValues(mode, keepY, alwaysRotateOffground, ninjaBridge, eagle, freelook, blockPicker);
    }

    @Override
    public void onEnable() {
        rotations = new FixedRotations(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

        oldYaw = mc.thePlayer.rotationYaw;
        oldPitch = mc.thePlayer.rotationPitch;

        if(freelook.getValue()) {
            Vestige.instance.getCameraHandler().setFreelooking(true);
            freelooking = true;
        }

        lastGroundY = mc.thePlayer.onGround ? mc.thePlayer.posY : Math.floor(mc.thePlayer.posY);

        started = false;

        blocksPlaced = 0;

        counter = ticks = 0;

        if(mode.is("Godbridge")) {
            float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);

            float roundedYaw = (float) (Math.round(yaw * 90) / 90.0);

            rotations.updateRotations(roundedYaw - 135, 76F);

            mc.thePlayer.rotationYaw = rotations.getYaw();
            mc.thePlayer.rotationPitch = rotations.getPitch();

            blocksPlaced = 1;
        } else if(mode.is("No sprint")) {
            rotations.updateRotations(mc.thePlayer.rotationYaw - 180, 76F);

            mc.thePlayer.rotationYaw = rotations.getYaw();
            mc.thePlayer.rotationPitch = rotations.getPitch();

            if(ninjaBridge.getValue()) {
                mc.gameSettings.keyBindForward.pressed = false;
                mc.gameSettings.keyBindBack.pressed = true;
            } else {
                invertKeyPresses();
            }
        }

        oldSlot = mc.thePlayer.inventory.currentItem;

        pickBlock();
    }

    @Override
    public void onDisable() {
        KeyboardUtil.resetKeybindings(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak);

        mc.gameSettings.keyBindUseItem.pressed = false;

        if(freelooking) {
            mc.thePlayer.rotationYaw = Vestige.instance.getCameraHandler().getYaw();
            mc.thePlayer.rotationPitch = Vestige.instance.getCameraHandler().getPitch();

            Vestige.instance.getCameraHandler().setFreelooking(false);
            freelooking = false;
        }

        switchToOriginalSlot();
    }

    private void switchToOriginalSlot() {
        if(!blockPicker.is("None")) {
            mc.thePlayer.inventory.currentItem = oldSlot;
        }

        Vestige.instance.getSlotSpoofHandler().stopSpoofing();
    }

    private void pickBlock() {
        if(!blockPicker.is("None")) {
            for(int i = 8; i >= 0; i--) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                if(stack != null && stack.getItem() instanceof ItemBlock && !PlayerUtil.isBlockBlacklisted(stack.getItem()) && stack.stackSize > 0) {
                    mc.thePlayer.inventory.currentItem = i;
                    break;
                }
            }
        }

        if(blockPicker.is("Spoof")) {
            Vestige.instance.getSlotSpoofHandler().startSpoofing(oldSlot);
        }
    }

    @Listener
    public void onTick(TickEvent event) {
        pickBlock();

        if(mc.thePlayer.onGround || !keepY.getValue()) {
            lastGroundY = mc.thePlayer.posY;
        }

        boolean isOverAir = WorldUtil.isAirOrLiquid(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));

        switch (mode.getValue()) {
            case "Godbridge":
                if (blocksPlaced >= 9) {
                    mc.gameSettings.keyBindJump.pressed = true;
                    blocksPlaced = 0;
                } else {
                    mc.gameSettings.keyBindJump.pressed = false;
                }

                if (started) {
                    mc.gameSettings.keyBindUseItem.pressed = true;
                    mc.rightClickDelayTimer = 0;

                    mc.gameSettings.keyBindSneak.pressed = false;
                } else {
                    mc.gameSettings.keyBindSneak.pressed = true;
                    mc.gameSettings.keyBindUseItem.pressed = false;
                }

                mc.gameSettings.keyBindBack.pressed = true;
                mc.gameSettings.keyBindRight.pressed = true;
                mc.gameSettings.keyBindForward.pressed = false;
                mc.gameSettings.keyBindLeft.pressed = false;

                if (Math.abs(mc.thePlayer.posX - mc.thePlayer.lastTickPosX) < 0.01 && Math.abs(mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) < 0.01 && WorldUtil.isAirOrLiquid(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ))) {
                    started = true;
                }
                break;
            case "No sprint":
                ticks++;

                if (!ninjaBridge.getValue()) {
                    invertKeyPresses();
                }

                if(eagle.getValue() && isOverAir && mc.thePlayer.onGround && (blocksPlaced == 0 || blocksPlaced % 3 != 0 || mc.thePlayer.isPotionActive(Potion.moveSpeed))) {
                    mc.gameSettings.keyBindSneak.pressed = true;
                } else {
                    KeyboardUtil.resetKeybinding(mc.gameSettings.keyBindSneak);
                }
                break;
        }
    }

    @Listener
    public void onUpdate(UpdateEvent event) {
        switch (mode.getValue()) {
            case "No sprint":
                if(!ninjaBridge.getValue()) {
                    invertKeyPresses();
                }
                break;
        }
    }

    @Listener
    public void onRender(RenderEvent event) {
        BlockInfo info = WorldUtil.getBlockUnder(mode.is("Sprint") ? lastGroundY : mc.thePlayer.posY, 3);

        switch (mode.getValue()) {
            case "Sprint":
                mc.gameSettings.keyBindUseItem.pressed = false;

                boolean jumping = mc.thePlayer.onGround && mc.gameSettings.keyBindJump.isKeyDown();

                if(WorldUtil.isAirOrLiquid(new BlockPos(mc.thePlayer.posX, lastGroundY - 1, mc.thePlayer.posZ)) && info != null && info.getFacing() != EnumFacing.DOWN && (info.getFacing() != EnumFacing.UP || !keepY.getValue()) && !jumping) {
                    float yaw = (freelooking ? Vestige.instance.getCameraHandler().getYaw() : oldYaw) - 180;

                    for(float pitch = 40F; pitch <= 90F; pitch += 0.1F) {
                        rotations.updateRotations(yaw, pitch);

                        MovingObjectPosition result = WorldUtil.raytrace(rotations.getYaw(), rotations.getPitch());

                        if(result != null && result.getBlockPos().equals(info.getPos()) && result.sideHit == info.getFacing()) {
                            mc.thePlayer.rotationYaw = rotations.getYaw();
                            mc.thePlayer.rotationPitch = rotations.getPitch();

                            mc.gameSettings.keyBindUseItem.pressed = true;
                            mc.rightClickDelayTimer = 0;
                            break;
                        }
                    }

                    invertKeyPresses();
                } else {
                    if(mc.thePlayer.onGround || !alwaysRotateOffground.getValue()) {
                        mc.thePlayer.rotationYaw = freelooking ? Vestige.instance.getCameraHandler().getYaw() : oldYaw;
                        mc.thePlayer.rotationPitch = oldPitch;

                        KeyboardUtil.resetKeybindings(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft,
                                mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak);
                    } else {
                        mc.thePlayer.rotationYaw = oldYaw - 180;
                        mc.thePlayer.rotationPitch = 77F;

                        invertKeyPresses();
                    }
                }
                break;
            case "No sprint":
                if(freelooking) {
                    mc.thePlayer.rotationYaw = Vestige.instance.getCameraHandler().getYaw() - 180;
                }

                mc.gameSettings.keyBindUseItem.pressed = false;

                if(WorldUtil.isAirOrLiquid(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)) && info != null && info.getFacing() != EnumFacing.DOWN) {
                    boolean found = false;

                    for(float pitch = 0F; pitch <= 90F; pitch += (pitch < 60 ? 0.2F : 0.1F)) {
                        rotations.updateRotations(mc.thePlayer.rotationYaw, pitch);

                        MovingObjectPosition result = WorldUtil.raytrace(rotations.getYaw(), rotations.getPitch());

                        if(result != null && result.getBlockPos().equals(info.getPos()) && result.sideHit == info.getFacing()) {
                            if(result.sideHit == info.getFacing()) {
                                mc.thePlayer.rotationYaw = rotations.getYaw();
                                mc.thePlayer.rotationPitch = rotations.getPitch();

                                mc.gameSettings.keyBindUseItem.pressed = true;
                                mc.rightClickDelayTimer = 0;

                                mc.gameSettings.keyBindSneak.pressed = false;

                                found = true;
                            }
                            break;
                        }
                    }

                    if(!found) {
                        mc.gameSettings.keyBindSneak.pressed = true;
                    }
                }

                if(ninjaBridge.getValue()) {
                    mc.gameSettings.keyBindForward.pressed = false;
                    mc.gameSettings.keyBindBack.pressed = true;

                    boolean hasSpeed = mc.thePlayer.isPotionActive(Potion.moveSpeed);

                    boolean left = hasSpeed ? ticks % 2 == 0 : ticks % 5 == 0;
                    boolean right = hasSpeed ? ticks % 2 == 1 : ticks % 5 == 1;

                    if(left) {
                        mc.gameSettings.keyBindLeft.pressed = true;
                        mc.gameSettings.keyBindRight.pressed = false;
                    } else if(right) {
                        mc.gameSettings.keyBindRight.pressed = true;
                        mc.gameSettings.keyBindLeft.pressed = false;
                    } else {
                        mc.gameSettings.keyBindLeft.pressed = false;
                        mc.gameSettings.keyBindRight.pressed = false;
                    }
                } else {
                    invertKeyPresses();
                }
                break;
        }
    }

    private void invertKeyPresses() {
        boolean forward = KeyboardUtil.isPressed(mc.gameSettings.keyBindForward);
        boolean backward = KeyboardUtil.isPressed(mc.gameSettings.keyBindBack);
        boolean left = KeyboardUtil.isPressed(mc.gameSettings.keyBindLeft);
        boolean right = KeyboardUtil.isPressed(mc.gameSettings.keyBindRight);

        mc.gameSettings.keyBindForward.pressed = backward;
        mc.gameSettings.keyBindBack.pressed = forward;
        mc.gameSettings.keyBindLeft.pressed = right;
        mc.gameSettings.keyBindRight.pressed = left;
    }

    @Listener
    public void onSend(PacketSendEvent event) {
        if(event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement packet = event.getPacket();

            if(!packet.getPosition().equals(new BlockPos(-1, -1, -1))) {
                blocksPlaced++;
            } else {
                if(mode.is("Godbridge") && mc.thePlayer.ticksExisted % 2 == 0) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
