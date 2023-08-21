package cn.yapeteam.yolbi.module.impl.world;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.*;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.impl.combat.Killaura;
import cn.yapeteam.yolbi.module.impl.movement.Speed;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import cn.yapeteam.yolbi.util.player.*;
import cn.yapeteam.yolbi.util.world.BlockInfo;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;

public class Scaffold extends Module {

    private final ModeValue<String> rotationsTiming = new ModeValue<>("Rotations timing", "Always", "Always", "Over air", "Never");
    private final ModeValue<String> rotationsMode = new ModeValue<>("Rotations mode", () -> !rotationsTiming.is("Never"), "Block center", "Normal", "Block center", "Movement based");

    private final BooleanValue rotChangeOverAir = new BooleanValue("Rot change over air", () -> rotationsMode.is("Normal"), false);

    private final BooleanValue randomised = new BooleanValue("Randomised", () -> rotationsMode.is("Block center"), false);

    private final NumberValue<Integer> yawOffset = new NumberValue<>("Yaw offset", () -> rotationsMode.is("Movement based"), 180, 0, 180, 5);
    private final NumberValue<Double> pitchValue = new NumberValue<>("Pitch", () -> rotationsMode.is("Movement based"), 81.5, 70.0, 110.0, 0.5);

    private final ModeValue<String> noSprintTiming = new ModeValue<>("No sprint timing", "Always", "Always", "When rotating", "Never");
    private final ModeValue<String> noSprintMode = new ModeValue<>("No sprint", () -> !noSprintTiming.is("Never"), "Normal", "Normal", "Spoof");

    private final ModeValue<String> raytrace = new ModeValue<>("Raytrace", "Disabled", "Disabled", "Normal", "Legit");
    private final NumberValue<Double> negativeExpand = new NumberValue<>("Negative expand", 0.0, 0.0, 0.24, 0.01);
    private final NumberValue<Double> offGroundNegativeExpand = new NumberValue<>("Offground negative expand", 0.0, 0.0, 0.24, 0.01);

    private final NumberValue<Integer> delayBetweenPlacements = new NumberValue<>("Delay between placements", 0, 0, 10, 1);

    private final BooleanValue sneakOverAir = new BooleanValue("Sneak over air", true);

    private final BooleanValue moveFix = new BooleanValue("Move fix", false);

    private final BooleanValue strafe = new BooleanValue("Strafe", () -> !moveFix.getValue(), false);
    private final ModeValue<String> offGroundStrafe = new ModeValue<>("Off ground strafe", () -> strafe.getValue() && !moveFix.getValue(), "Disabled", "Disabled", "Enabled", "Keep movement");
    private final NumberValue<Double> strafeSpeed = new NumberValue<>("Speed", () -> strafe.getValue() && !moveFix.getValue(), 0.2, 0.1, 0.5, 0.005);
    private final NumberValue<Double> overAirSpeed = new NumberValue<>("Over air speed", () -> strafe.getValue() && !moveFix.getValue(), 0.1, 0.0, 0.5, 0.005);
    private final NumberValue<Double> offGroundSpeed = new NumberValue<>("Offground speed", () -> strafe.getValue() && !offGroundStrafe.is("Disabled") && !moveFix.getValue(), 0.2, 0.1, 0.5, 0.005);
    private final NumberValue<Double> strafeSpeedPotExtra = new NumberValue<>("Speed pot extra", () -> strafe.getValue() && !moveFix.getValue(), 0.2, 0.0, 0.2, 0.005);
    private final BooleanValue randomisedSpeed = new BooleanValue("Randomised speed", () -> strafe.getValue() && !moveFix.getValue(), false);

    private final ModeValue<String> jump = new ModeValue<>("Jump", "Disabled", "Disabled", "Enabled", "Hypixel");

    private final NumberValue<Integer> range = new NumberValue<>("Range", 2, 1, 4, 1);

    private final ModeValue<String> tower = new ModeValue<>("Tower", "None", "Vanilla", "NCP", "NCP2", "OldHypixel", "MMC", "None");

    private final ModeValue<String> blockPicker = new ModeValue<>("Block picker", "Switch", "None", "Switch", "Spoof");

    private final BooleanValue noSwing = new BooleanValue("No swing", false);

    private BlockInfo info;
    private Vec3 vec3;

    private double lastY;

    private FixedRotations rotations;

    private boolean isRotating;

    private double lastMotionX, lastMotionZ;

    private boolean startedLowhop;

    private int oldSlot;

    private int towerTicks;

    private int placeDelay;

    private boolean hadBlockInfo;

    public Scaffold() {
        super("Scaffold", ModuleCategory.WORLD);
        this.addValues(rotationsTiming, rotationsMode, rotChangeOverAir, randomised, yawOffset, pitchValue, noSprintTiming, noSprintMode, raytrace, negativeExpand, offGroundNegativeExpand, delayBetweenPlacements, sneakOverAir, moveFix, strafe, offGroundStrafe, strafeSpeed, overAirSpeed, offGroundSpeed, strafeSpeedPotExtra, randomisedSpeed, jump, range, tower, blockPicker, noSwing);
    }

    @Override
    public void onEnable() {
        rotations = new FixedRotations(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

        switch (rotationsMode.getValue()) {
            case "Normal":
                rotations.updateRotations(MathHelper.wrapAngleTo180_float(MovementUtil.getPlayerDirection() - 180), 81.5F);
                break;
            case "Movement based":
                rotations.updateRotations(MovementUtil.getPlayerDirection() - yawOffset.getValue(), pitchValue.getValue().floatValue());
                break;
        }

        info = null;
        vec3 = null;

        lastMotionX = lastMotionZ = 0;

        startedLowhop = false;

        towerTicks = 0;

        oldSlot = mc.thePlayer.inventory.currentItem;

        placeDelay = 0;
    }

    @Override
    public void onDisable() {
        KeyboardUtil.resetKeybindings(mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindSneak);

        switchToOriginalSlot();
    }

    private void switchToOriginalSlot() {
        if (!blockPicker.is("None")) {
            mc.thePlayer.inventory.currentItem = oldSlot;
        }

        YolBi.instance.getSlotSpoofHandler().stopSpoofing();
    }

    private void pickBlock() {
        if (!blockPicker.is("None")) {
            for (int i = 8; i >= 0; i--) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                if (stack != null && stack.getItem() instanceof ItemBlock && !PlayerUtil.isBlockBlacklisted(stack.getItem()) && stack.stackSize > 0) {
                    mc.thePlayer.inventory.currentItem = i;
                    break;
                }
            }
        }

        if (blockPicker.is("Spoof")) {
            YolBi.instance.getSlotSpoofHandler().startSpoofing(oldSlot);
        }
    }

    @Listener
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.ticksExisted < 10) {
            this.setEnabled(false);
            return;
        }

        pickBlock();

        if (!jump.is("Disabled") || YolBi.instance.getModuleManager().getModule(Speed.class).isEnabled()) {
            if (mc.thePlayer.onGround || mc.gameSettings.keyBindJump.isKeyDown()) {
                lastY = mc.thePlayer.posY;
            }
        } else {
            lastY = mc.thePlayer.posY;
        }

        info = WorldUtil.getBlockUnder(lastY, range.getValue());

        float yaw = rotations.getYaw();
        float pitch = rotations.getPitch();

        boolean isAirUnder = WorldUtil.isAirOrLiquid(new BlockPos(mc.thePlayer.posX, lastY - 1, mc.thePlayer.posZ));

        switch (rotationsMode.getValue()) {
            case "Normal":
                float[] normalRots = getNormalRotations(rotChangeOverAir.getValue());

                yaw = normalRots[0];
                pitch = normalRots[1];
                break;
            case "Block center":
                if (info != null && WorldUtil.negativeExpand(getNegativeExpand())) {
                    Vec3 vec = WorldUtil.getVec3(info.getPos(), info.getFacing(), randomised.getValue());

                    float[] rots = RotationsUtil.getRotationsToPosition(vec.xCoord, vec.yCoord, vec.zCoord);

                    yaw = rots[0];
                    pitch = rots[1];
                }
                break;
            case "Movement based":
                yaw = MovementUtil.getPlayerDirection() - yawOffset.getValue();

                if (info != null) {
                    if (info.getFacing() == EnumFacing.UP) {
                        pitch = 90F;
                    } else {
                        pitch = pitchValue.getValue().floatValue();
                    }
                }
                break;
        }

        rotations.updateRotations(yaw, pitch);

        isRotating = rotationsTiming.is("Always") || (rotationsTiming.is("Over air") && info != null && WorldUtil.negativeExpand(getNegativeExpand()));

        if (noSprintMode.is("Normal")) {
            if (noSprintTiming.is("Always") || ((noSprintTiming.is("When rotating") && isRotating))) {
                mc.gameSettings.keyBindSprint.setPressed(false);
                mc.thePlayer.setSprinting(false);
            }
        }

        if (jump.is("Enabled")) {
            if (mc.thePlayer.onGround && MovementUtil.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.jump();
            }
        }

        boolean placed = false;

        if (info != null) {
            placed = placeBlock();
        }

        if (!placed && mc.thePlayer.ticksExisted % 2 == 0) {
            Killaura killaura = YolBi.instance.getModuleManager().getModule(Killaura.class);

            if (!killaura.isEnabled() || killaura.getTarget() == null) {
                PacketUtil.sendBlocking(true, false);
            }
        }

        if (!placed && isAirUnder && sneakOverAir.getValue() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.gameSettings.keyBindSneak.setPressed(true);
        } else {
            KeyboardUtil.resetKeybinding(mc.gameSettings.keyBindSneak);
        }

        hadBlockInfo = info != null;

        placeDelay++;
    }

    private float[] getNormalRotations(boolean firstTickOverAir) {
        float yaw = MathHelper.wrapAngleTo180_float(rotations.getYaw());
        float pitch = rotations.getPitch();

        boolean condition = firstTickOverAir ? info != null && !hadBlockInfo : info != null && WorldUtil.negativeExpand(getNegativeExpand());

        if (condition) {
            BlockPos pos = info.getPos();
            EnumFacing facing = info.getFacing();

            BlockPos playerPos = mc.thePlayer.getPosition();

            switch (facing) {
                case EAST:
                    if (yaw > 136 || yaw < 44) {
                        if (playerPos.getZ() > pos.getZ()) {
                            yaw = 135;
                        } else {
                            yaw = 45;
                        }
                    }
                    break;
                case WEST:
                    if (yaw < -136 || yaw > -44) {
                        if (playerPos.getZ() > pos.getZ()) {
                            yaw = -135;
                        } else {
                            yaw = -45;
                        }
                    }
                    break;
                case NORTH:
                    if (yaw < -46 || yaw > 46) {
                        if (playerPos.getX() > pos.getX()) {
                            yaw = 45;
                        } else {
                            yaw = -45;
                        }
                    }
                    break;
                case SOUTH:
                    if (yaw < 134 && yaw > -134) {
                        if (playerPos.getX() > pos.getX()) {
                            yaw = 135;
                        } else {
                            yaw = -135;
                        }
                    }
                    break;
            }

            if (facing == EnumFacing.UP) {
                pitch = 90;
            } else {
                boolean found = false;

                for (float i = 75; i <= 85; i += 0.5F) {
                    MovingObjectPosition movingObjectPosition = WorldUtil.raytrace(yaw, i);

                    if (movingObjectPosition != null && movingObjectPosition.sideHit == info.getFacing()) {
                        pitch = i;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    pitch = 80F;
                }
            }
        }

        return new float[]{yaw, pitch};
    }

    @Listener
    public void onStrafe(StrafeEvent event) {
        if (moveFix.getValue() && isRotating) {
            event.setYaw(rotations.getYaw());

            float diff = MathHelper.wrapAngleTo180_float(MathHelper.wrapAngleTo180_float(rotations.getYaw()) - MathHelper.wrapAngleTo180_float(MovementUtil.getPlayerDirection())) + 22.5F;

            if (diff < 0) {
                diff = 360 + diff;
            }

            int a = (int) (diff / 45.0);

            //LogUtil.addChatMessage("Diff " + diff + " : Test " + a);

            float value = event.getForward() != 0 ? Math.abs(event.getForward()) : Math.abs(event.getStrafe());

            float forward = value;
            float strafe = 0;

            for (int i = 0; i < 8 - a; i++) {
                float[] dirs = MovementUtil.incrementMoveDirection(forward, strafe);

                forward = dirs[0];
                strafe = dirs[1];
            }

            event.setForward(forward);
            event.setStrafe(strafe);
        }
    }

    @Listener
    public void onJump(JumpEvent event) {
        if (moveFix.getValue() && isRotating) {
            event.setYaw(rotations.getYaw());
        }
    }

    @Listener
    public void onEntityAction(EntityActionEvent event) {
        if (noSprintMode.is("Spoof")) {
            if (noSprintTiming.is("Always") || (noSprintTiming.is("When rotating") && isRotating)) {
                event.setSprinting(false);
            }
        }
    }

    @Listener
    public void onMove(MoveEvent event) {
        if (strafe.getValue() && !moveFix.getValue()) {
            double a = randomisedSpeed.getValue() ? Math.random() * 0.0001 : 0;

            if (mc.thePlayer.onGround || offGroundStrafe.is("Enabled")) {
                if (!WorldUtil.isBlockUnder(2)) {
                    MovementUtil.strafeNoTargetStrafe(event, overAirSpeed.getValue() - a);
                } else if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    MovementUtil.strafeNoTargetStrafe(event, strafeSpeed.getValue() + MovementUtil.getSpeedAmplifier() * strafeSpeedPotExtra.getValue() - a);
                } else {
                    MovementUtil.strafeNoTargetStrafe(event, offGroundSpeed.getValue() + MovementUtil.getSpeedAmplifier() * strafeSpeedPotExtra.getValue() - a);
                }

                lastMotionX = event.getX();
                lastMotionZ = event.getZ();
            } else if (offGroundStrafe.is("Keep movement")) {
                if (lastMotionX != 0 || lastMotionZ != 0) {
                    event.setX((mc.thePlayer.motionX = lastMotionX) - a);
                    event.setZ((mc.thePlayer.motionZ = lastMotionZ) - a);
                }
            }
        }

        if (jump.is("Hypixel")) {
            if (!MovementUtil.isMoving()) {
                startedLowhop = false;
            }

            if (mc.thePlayer.onGround) {
                if (MovementUtil.isMoving()) {
                    if (startedLowhop && !mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (!WorldUtil.isAirOrLiquid(new BlockPos(mc.thePlayer.posX + event.getX(), mc.thePlayer.posY - 1, mc.thePlayer.posZ + event.getZ()))) {
                            event.setY(mc.thePlayer.motionY = 0.0005);
                        }
                    } else {
                        MovementUtil.jump(event);
                    }
                }
            } else {
                if (event.getY() > 0.3) {
                    startedLowhop = true;
                }
            }
        }

        tower(event);
    }

    public void tower(MoveEvent event) {
        boolean blockUnder = WorldUtil.isBlockUnder(2);
        boolean spacePressed = mc.gameSettings.keyBindJump.isKeyDown();
        boolean isMoving = MovementUtil.isMoving();

        switch (tower.getValue()) {
            case "Vanilla":
                if (blockUnder && !isMoving) {
                    if (spacePressed) {
                        event.setY(mc.thePlayer.motionY = 1);
                    } else if (!mc.thePlayer.onGround) {
                        event.setY(mc.thePlayer.motionY = -1);
                    }
                }
                break;
            case "MMC":
                if (spacePressed && blockUnder) {
                    MovementUtil.jump(event);
                }
                break;
            case "NCP":
                if (spacePressed && blockUnder && !MovementUtil.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        MovementUtil.jump(event);
                    } else if (mc.thePlayer.posY - Math.floor(mc.thePlayer.posY) < 0.05) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                        MovementUtil.jump(event);
                    }
                }
                break;
            case "NCP2":
                if (spacePressed) {
                    if (mc.thePlayer.onGround) {
                        MovementUtil.jump(event);
                        towerTicks = 0;
                    } else if (towerTicks == 2) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
                        event.setY(mc.thePlayer.motionY = 0);
                        mc.thePlayer.onGround = true;
                    }
                }
                towerTicks++;
                break;
            case "OldHypixel":
                if (mc.thePlayer.onGround) {
                    towerTicks = 0;
                }

                if (spacePressed && isMoving) {
                    if (mc.thePlayer.onGround) {
                        MovementUtil.jump(event);
                        towerTicks = 0;
                    } else if (towerTicks == 3) {
                        event.setY(mc.thePlayer.motionY = 0);
                    }

                    towerTicks++;
                }
                break;
        }
    }

    @Listener
    public void onMotion(MotionEvent event) {
        if (isRotating) {
            event.setYaw(rotations.getYaw());
            event.setPitch(rotations.getPitch());
        }
    }

    private boolean raytrace() {
        MovingObjectPosition movingObjectPosition;

        if (raytrace.is("Legit")) {
            movingObjectPosition = WorldUtil.raytraceLegit(rotations.getYaw(), rotations.getPitch(), rotations.getLastYaw(), rotations.getLastPitch());
            //movingObjectPosition = WorldUtil.raytraceLegit(lastYaw, lastPitch, lastLastYaw, lastLastPitch);
        } else {
            movingObjectPosition = WorldUtil.raytrace(rotations.getYaw(), rotations.getPitch());
        }

        if (movingObjectPosition != null && movingObjectPosition.sideHit == info.getFacing() && movingObjectPosition.getBlockPos().equals(info.getPos())) {
            vec3 = movingObjectPosition.hitVec;
            return true;
        }

        return false;
    }

    public boolean placeBlock() {
        ItemStack stack = mc.thePlayer.getHeldItem();

        if (info != null && stack != null && stack.getItem() instanceof ItemBlock) {
            if (placeDelay >= delayBetweenPlacements.getValue()) {
                if (raytrace.is("Disabled") || raytrace()) {
                    if (WorldUtil.negativeExpand(getNegativeExpand())) {
                        return sendPlacing();
                    }
                }
            }
        }

        return false;
    }

    public boolean sendPlacing() {
        ItemStack stack = mc.thePlayer.getHeldItem();
        Vec3 vec = !raytrace.is("None") && vec3 != null ? vec3 : WorldUtil.getVec3(info.getPos(), info.getFacing(), true);

        boolean success = mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, info.getPos(), info.getFacing(), vec);

        if (success) {
            if (noSwing.getValue()) {
                PacketUtil.sendPacket(new C0APacketAnimation());
            } else {
                mc.thePlayer.swingItem();
            }

            BlockInfo prevBlockInfo = info;

            placeDelay = 0;
        }

        vec3 = null;

        return success;
    }

    public double getNegativeExpand() {
        return mc.gameSettings.keyBindJump.isKeyDown() || !mc.thePlayer.onGround ? offGroundNegativeExpand.getValue() : negativeExpand.getValue();
    }

}
