package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.PacketReceiveEvent;
import cn.yapeteam.yolbi.event.impl.player.*;
import cn.yapeteam.yolbi.event.impl.render.Render3DEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import cn.yapeteam.yolbi.util.player.MovementUtil;
import cn.yapeteam.yolbi.util.player.PlayerUtil;
import cn.yapeteam.yolbi.util.player.RotationsUtil;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.util.world.BlockInfo;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.BlockSlime;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;

@ModuleInfo(name = "Speed", category = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    public final ModeValue<String> mode = new ModeValue<>("Mode", "Vanilla", "Vanilla", "NCP", "Watchdog", "Blocksmc", "MMC", "Strafe", "Fake strafe", "KKC");

    private final NumberValue<Double> vanillaSpeed = new NumberValue<>("Vanilla speed", () -> mode.is("Vanilla"), 1.0, 0.2, 9.0, 0.1);
    private final BooleanValue autoJump = new BooleanValue("Autojump", () -> mode.is("Vanilla") || mode.is("Strafe"), true);

    private final ModeValue<String> ncpMode = new ModeValue<>("NCP Mode", () -> mode.is("NCP"), "Hop", "Hop", "Updated Hop");
    private final BooleanValue damageBoost = new BooleanValue("Damage Boost", () -> mode.is("NCP") && ncpMode.is("Updated Hop"), true);

    public final ModeValue<String> watchdogMode = new ModeValue<>("Watchdog Mode", () -> mode.is("Watchdog"), "Strafe", "Strafe", "Semi-Strafe", "Strafeless", "Ground");

    private final BooleanValue fast = new BooleanValue("Fast", () -> mode.is("Watchdog") && (watchdogMode.is("Strafe") || watchdogMode.is("Strafeless")), true);

    private final NumberValue<Double> attributeSpeedOffground = new NumberValue<>("Attribute speed offground", () -> mode.is("Watchdog") && watchdogMode.is("Strafe"), 0.023, 0.02, 0.026, 0.001);

    private final NumberValue<Double> mult = new NumberValue<>("Mult", () -> mode.is("Watchdog") && watchdogMode.is("Strafeless") && fast.getValue(), 1.24, 1.0, 1.3, 0.005);
    private final NumberValue<Double> speedPotMult = new NumberValue<>("Speed pot mult", () -> mode.is("Watchdog") && watchdogMode.is("Strafeless") && fast.getValue(), 1.24, 1.0, 1.3, 0.005);

    private final BooleanValue fullScaffold = new BooleanValue("Full scaffold", () -> mode.is("MMC"), false);

    private final BooleanValue allDirSprint = new BooleanValue("All directions sprint", () -> mode.is("Strafe"), true);
    private final NumberValue<Integer> minHurtTime = new NumberValue<>("Min hurttime", () -> mode.is("Strafe"), 10, 0, 10, 1);

    private final BooleanValue sprint = new BooleanValue("Sprint", () -> mode.is("Fake strafe"), true);
    private final BooleanValue rotate = new BooleanValue("Rotate", () -> mode.is("Fake strafe"), false);
    private final BooleanValue groundStrafe = new BooleanValue("Ground Strafe", () -> mode.is("Fake strafe"), false);
    private final ModeValue<String> velocityMode = new ModeValue<>("Velocity handling", () -> mode.is("Fake strafe"), "Ignore", "Ignore", "Vertical", "Legit");
    private final ModeValue<String> clientSpeed = new ModeValue<>("Client speed", () -> mode.is("Fake strafe"), "Normal", "Normal", "Custom");
    private final NumberValue<Double> customClientSpeed = new NumberValue<>("Custom client speed", () -> mode.is("Fake strafe") && clientSpeed.is("Custom"), 0.5, 0.15, 1.0, 0.025);
    private final BooleanValue fakeFly = new BooleanValue("Fake fly", () -> mode.is("Fake strafe"), false);
    private final BooleanValue renderRealPosBox = new BooleanValue("Render box at real pos", () -> mode.is("Fake strafe"), true);

    private final ModeValue<String> timerMode = new ModeValue<>("Timer mode", () -> mode.is("NCP"), "None", "None", "Bypass", "Custom");
    private final NumberValue<Double> customTimer = new NumberValue<>("Custom timer", () -> (mode.is("NCP") && timerMode.is("Custom")) || mode.is("Watchdog"), 1.0, 0.1, 3.0, 0.05);

    private double speed;

    private boolean prevOnGround;

    private int counter;
    private int ticks;

    private boolean takingVelocity;
    private double velocityDist;

    private float lastDirection;

    private float lastYaw;

    private double motionX, motionY, motionZ;

    @Getter
    private double actualX, actualY, actualZ, lastActualX, lastActualY, lastActualZ;

    private boolean actualGround;

    private boolean started, firstJumpDone;

    private boolean wasCollided;

    private int oldSlot;

    private final ArrayList<BlockPos> barriers = new ArrayList<>();

    public Speed() {
        this.addValues(mode, vanillaSpeed, autoJump, ncpMode, damageBoost, watchdogMode, fast, mult, speedPotMult, attributeSpeedOffground, fullScaffold, allDirSprint, minHurtTime, sprint, rotate, groundStrafe, velocityMode, clientSpeed, customClientSpeed, fakeFly, renderRealPosBox, timerMode, customTimer);
    }

    @Override
    public void onEnable() {
        prevOnGround = false;
        speed = 0.28;

        ticks = counter = 0;

        started = firstJumpDone = false;

        takingVelocity = false;

        motionX = mc.thePlayer.motionX;
        motionY = mc.thePlayer.motionY;
        motionZ = mc.thePlayer.motionZ;

        actualX = mc.thePlayer.posX;
        actualY = mc.thePlayer.posY;
        actualZ = mc.thePlayer.posZ;

        actualGround = mc.thePlayer.onGround;

        lastDirection = MovementUtil.getPlayerDirection();

        lastYaw = mc.thePlayer.rotationYaw;

        oldSlot = mc.thePlayer.inventory.currentItem;

        wasCollided = false;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;

        switch (mode.getValue()) {
            case "Vulcan":
                mc.thePlayer.inventory.currentItem = oldSlot;
                break;
            case "Watchdog":
                if (watchdogMode.is("Strafe")) {
                    mc.thePlayer.motionX *= 0.2;
                    mc.thePlayer.motionZ *= 0.2;
                }
                break;
            case "Fake strafe":
                mc.thePlayer.setPosition(actualX, actualY, actualZ);
                mc.thePlayer.motionX = motionX;
                mc.thePlayer.motionY = motionY;
                mc.thePlayer.motionZ = motionZ;

                mc.thePlayer.onGround = actualGround;
                break;
        }

        if (!barriers.isEmpty()) {
            for (BlockPos pos : barriers) {
                mc.theWorld.setBlockToAir(pos);
            }

            barriers.clear();
        }
    }

    @Listener
    public void onStrafe(StrafeEvent event) {
        switch (mode.getValue()) {
            case "Watchdog":
                if (watchdogMode.is("Test")) {
                    if (!mc.thePlayer.isSprinting()) {
                        event.setAttributeSpeed(event.getAttributeSpeed() * 1.3F);
                    }

                    if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.jump();
                    }
                }
                break;
            case "Strafe":
                if (allDirSprint.getValue()) {
                    if (!mc.thePlayer.isSprinting()) {
                        event.setAttributeSpeed(event.getAttributeSpeed() * 1.3F);
                    }
                }

                if (autoJump.getValue() && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.jump();
                }
                break;
        }
    }

    @Listener
    public void onJump(JumpEvent event) {
        switch (mode.getValue()) {
            case "Strafe":
                if (allDirSprint.getValue()) {
                    event.setBoosting(MovementUtil.isMoving());
                    event.setYaw(MovementUtil.getPlayerDirection());
                }
                break;
            case "Watchdog":
                if (watchdogMode.is("Test")) {
                    event.setBoosting(MovementUtil.isMoving());
                    event.setYaw(MovementUtil.getPlayerDirection());
                }
                break;
            case "Test":
            case "Test2":
                event.setBoosting(MovementUtil.isMoving());
                event.setYaw(MovementUtil.getPlayerDirection());
                break;
        }
    }

    @Listener
    public void onUpdate(UpdateEvent event) {
        switch (mode.getValue()) {
            case "Vulcan":
                for (int i = 8; i >= 0; i--) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                    if (stack != null && stack.getItem() instanceof ItemBlock && !PlayerUtil.isBlockBlacklisted(stack.getItem())) {
                        mc.thePlayer.inventory.currentItem = i;
                        break;
                    }
                }

                if (mc.thePlayer.onGround) {
                    if (MovementUtil.isMoving()) {
                        mc.thePlayer.jump();
                        ticks = 0;
                    }
                } else {
                    if (ticks == 4) {
                        if (started) {
                            mc.thePlayer.motionY = -1;
                        }

                        double x = mc.thePlayer.motionX > 0 ? 1.5 : -1.5;
                        double z = mc.thePlayer.motionZ > 0 ? 1.5 : -1.5;

                        mc.playerController.syncCurrentPlayItem();
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY - 2, mc.thePlayer.posZ + z), EnumFacing.UP.getIndex(), mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem), 0.5F, 1, 0.5F));
                        mc.thePlayer.swingItem();

                        started = true;
                    }

                    ticks++;
                }
                break;
            case "MMC":
                if (!started || fullScaffold.getValue()) break;
            case "Test":
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = 0;
                }
                break;
        }
    }


    @Listener
    public void onMove(MoveEvent event) {
        if (!takingVelocity && mc.thePlayer.onGround) {
        }

        double velocityExtra = 0.28 + MovementUtil.getSpeedAmplifier() * 0.07;

        float direction = MathHelper.wrapAngleTo180_float(MovementUtil.getPlayerDirection());

        float forward = mc.thePlayer.moveForward;
        float strafe = mc.thePlayer.moveStrafing;

        switch (mode.getValue()) {
            case "Vanilla":
                if (mc.thePlayer.onGround && MovementUtil.isMoving() && autoJump.getValue()) {
                    event.setY(mc.thePlayer.motionY = (double) mc.thePlayer.getJumpUpwardsMotion());
                }

                MovementUtil.strafe(event, vanillaSpeed.getValue());
                break;
            case "NCP":
                switch (ncpMode.getValue()) {
                    case "Hop":
                        if (mc.thePlayer.onGround) {
                            prevOnGround = true;

                            if (MovementUtil.isMoving()) {
                                event.setY(mc.thePlayer.motionY = (double) mc.thePlayer.getJumpUpwardsMotion());

                                speed *= 0.91;
                                speed += (ticks >= 8 ? 0.2 : 0.15) + mc.thePlayer.getAIMoveSpeed();

                                ticks = 0;
                            }
                        } else if (prevOnGround) {
                            speed *= 0.58;
                            speed += 0.026;

                            prevOnGround = false;
                        } else {
                            speed *= 0.91;
                            speed += 0.026;

                            ticks++;
                        }

                        if (speed > 0.2) {
                            speed -= 1E-6;
                        }
                        break;
                    case "Updated Hop":
                        if (mc.thePlayer.onGround) {
                            prevOnGround = true;

                            if (MovementUtil.isMoving()) {
                                MovementUtil.jump(event);

                                speed *= 0.91;

                                if (takingVelocity && damageBoost.getValue()) {
                                    speed = velocityDist + velocityExtra;
                                }

                                speed += 0.2 + mc.thePlayer.getAIMoveSpeed();
                            }
                        } else if (prevOnGround) {
                            speed *= 0.53;

                            if (takingVelocity && damageBoost.getValue()) {
                                speed = velocityDist + velocityExtra;
                            }

                            speed += 0.026F;

                            prevOnGround = false;
                        } else {
                            speed *= 0.91;

                            if (takingVelocity && damageBoost.getValue()) {
                                speed = velocityDist + velocityExtra;
                            }

                            speed += 0.026F;
                        }
                        break;
                }

                switch (timerMode.getValue()) {
                    case "None":
                        mc.timer.timerSpeed = 1F;
                        break;
                    case "Bypass":
                        mc.timer.timerSpeed = 1.08F;
                        break;
                    case "Custom":
                        mc.timer.timerSpeed = customTimer.getValue().floatValue();
                        break;
                }

                MovementUtil.strafe(event, speed);
                break;
            case "Watchdog":
                switch (watchdogMode.getValue()) {
                    case "Strafe":
                        if (mc.thePlayer.onGround) {
                            if (MovementUtil.isMoving()) {
                                prevOnGround = true;

                                MovementUtil.jump(event);

                                speed = 0.585 + MovementUtil.getSpeedAmplifier() * 0.065;
                            }
                        } else if (prevOnGround) {
                            if (ticks++ % 5 > 0 && fast.getValue()) {
                                speed *= 0.65F;
                            } else {
                                speed *= 0.53F;
                            }
                            prevOnGround = false;
                        } else {
                            speed = Math.min(speed, 0.35 + MovementUtil.getSpeedAmplifier() * 0.02);

                            speed *= 0.91F;

                            speed += attributeSpeedOffground.getValue().floatValue() * 0.98F;
                        }

                        MovementUtil.strafe(event, speed);
                        break;
                    case "Semi-Strafe":
                        if (mc.thePlayer.onGround) {
                            prevOnGround = true;

                            if (MovementUtil.isMoving()) {
                                MovementUtil.jump(event);

                                speed = 0.6 + MovementUtil.getSpeedAmplifier() * 0.075;
                            }
                        } else if (prevOnGround) {
                            speed *= 0.54F;
                            prevOnGround = false;
                        } else {
                            speed *= 0.91F;

                            speed += (mc.thePlayer.isSprinting() ? 0.026F : 0.02F) * 0.98F;
                        }

                        direction = MovementUtil.getPlayerDirection();

                        if (!mc.thePlayer.onGround) {
                            float dirChange = Math.abs(direction - lastDirection);

                            if (dirChange > 180) {
                                dirChange = 360 - dirChange;
                            }

                            double reduceMult = 1 - dirChange * 0.01;

                            speed *= reduceMult;

                            speed = Math.max(speed, 0.09);
                        }

                        if (mc.thePlayer.isCollidedHorizontally) {
                            speed = 0.09;
                        }

                        MovementUtil.strafe(event, speed);

                        lastDirection = direction;
                        break;
                    case "Strafeless":
                        if (MovementUtil.isMoving()) {
                            if (mc.thePlayer.onGround) {
                                prevOnGround = true;

                                MovementUtil.jump(event);

                                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                                    MovementUtil.strafeNoTargetStrafe(event, 0.59 - Math.random() * 0.001 + MovementUtil.getSpeedAmplifier() * 0.08);
                                } else {
                                    MovementUtil.strafeNoTargetStrafe(event, 0.6 - Math.random() * 0.001);
                                }
                            } else {
                                if (prevOnGround) {
                                    if (mc.thePlayer.isSprinting()) {
                                        if (++counter > 1 && fast.getValue()) {
                                            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                                                event.setX(event.getX() * speedPotMult.getValue());
                                                event.setZ(event.getZ() * speedPotMult.getValue());
                                            } else {
                                                event.setX(event.getX() * mult.getValue());
                                                event.setZ(event.getZ() * mult.getValue());
                                            }
                                        }
                                    }

                                    prevOnGround = false;
                                }
                            }
                        }

                        break;
                    case "Ground":
                        if (mc.thePlayer.onGround) {
                            ticks = 0;

                            if (!started) {
                                MovementUtil.jump(event);
                                MovementUtil.strafe(event, 0.55 + MovementUtil.getSpeedAmplifier() * 0.07);
                                started = true;
                            } else {
                                event.setY(mc.thePlayer.motionY = 0.0005);
                                firstJumpDone = true;

                                speed = 0.335 + MovementUtil.getSpeedAmplifier() * 0.045F;
                            }
                        } else {
                            ticks++;

                            if (speed > 0.28) {
                                speed *= 0.995;
                            }
                        }

                        if (firstJumpDone && ticks <= 2) {
                            MovementUtil.strafe(event, speed);
                        }
                        break;
                }

                mc.timer.timerSpeed = customTimer.getValue().floatValue();
                break;
            case "Blocksmc":
                if (mc.thePlayer.onGround) {
                    prevOnGround = true;

                    if (MovementUtil.isMoving()) {
                        MovementUtil.jump(event);

                        speed = 0.57 + MovementUtil.getSpeedAmplifier() * 0.065;

                        if (takingVelocity && damageBoost.getValue()) {
                            speed = velocityDist + velocityExtra;
                        }

                        ticks = 1;
                    }
                } else if (prevOnGround) {
                    speed *= 0.53;

                    if (takingVelocity && damageBoost.getValue()) {
                        speed = velocityDist + velocityExtra;
                    }

                    speed += 0.026F;

                    prevOnGround = false;
                } else {
                    speed *= 0.91;

                    if (takingVelocity && damageBoost.getValue()) {
                        speed = velocityDist + velocityExtra;
                    }

                    speed += 0.026F;
                }

                if (takingVelocity) {
                    ticks = -7;
                }

                if (++ticks == 0 && !mc.thePlayer.onGround) {
                    speed = 0.28 + MovementUtil.getSpeedAmplifier() * 0.065;
                }

                MovementUtil.strafe(event, speed);
                break;
            case "Strafe":
                if (mc.thePlayer.hurtTime <= minHurtTime.getValue()) {
                    MovementUtil.strafe(event);
                }
                break;
            case "MMC":
                if (started) {
                    BlockInfo blockOver = WorldUtil.getBlockInfo(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ, 2);
                    BlockInfo blockUnder = WorldUtil.getBlockUnder(mc.thePlayer.posY, 2);

                    counter++;

                    if (fullScaffold.getValue()) {
                        if (counter % 14 >= 12) {
                            MovementUtil.strafe(event, 0.04);
                        } else {
                            MovementUtil.strafe(event, 0.495);
                        }

                        event.setY(mc.thePlayer.motionY = 0);

                        if (counter % 2 == 0) {
                            if (blockOver != null) {
                                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockOver.getPos(), blockOver.getFacing(), WorldUtil.getVec3(blockOver.getPos(), blockOver.getFacing(), false))) {
                                    PacketUtil.sendPacket(new C0APacketAnimation());
                                }
                            }
                        } else {
                            if (blockUnder != null) {
                                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockUnder.getPos(), blockUnder.getFacing(), WorldUtil.getVec3(blockUnder.getPos(), blockUnder.getFacing(), false))) {
                                    PacketUtil.sendPacket(new C0APacketAnimation());
                                }
                            }
                        }
                    } else {
                        if (blockOver != null) {
                            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockOver.getPos(), blockOver.getFacing(), WorldUtil.getVec3(blockOver.getPos(), blockOver.getFacing(), false))) {
                                PacketUtil.sendPacket(new C0APacketAnimation());
                            }
                        }
                    }
                } else {
                    float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);

                    double x = 0;
                    double z = 0;

                    EnumFacing facing = EnumFacing.UP;

                    if (yaw > 135 || yaw < -135) {
                        z = 1;
                        facing = EnumFacing.NORTH;
                    } else if (yaw > -135 && yaw < -45) {
                        x = -1;
                        facing = EnumFacing.EAST;
                    } else if (yaw > -45 && yaw < 45) {
                        z = -1;
                        facing = EnumFacing.SOUTH;
                    } else if (yaw > 45 && yaw < 135) {
                        x = 1;
                        facing = EnumFacing.WEST;
                    }

                    BlockPos pos;

                    switch (++counter) {
                        case 1:
                            pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY - 1, mc.thePlayer.posZ + z);

                            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, EnumFacing.UP, WorldUtil.getVec3(pos, EnumFacing.DOWN, true));
                            break;
                        case 2:
                            pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);

                            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, EnumFacing.UP, WorldUtil.getVec3(pos, EnumFacing.DOWN, true));
                            break;
                        case 3:
                            pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + 1, mc.thePlayer.posZ + z);

                            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, EnumFacing.UP, WorldUtil.getVec3(pos, EnumFacing.DOWN, true));
                            break;
                        case 5:
                            pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + 2, mc.thePlayer.posZ + z);

                            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, WorldUtil.getVec3(pos, facing, true));

                            started = true;
                            counter = 0;
                            break;
                    }

                    PacketUtil.sendPacket(new C0APacketAnimation());

                    MovementUtil.strafe(event, 0.04);
                }
                break;
            case "Fake strafe":
                double distance = Math.hypot(mc.thePlayer.posX - actualX, mc.thePlayer.posZ - actualZ);

                if (fakeFly.getValue()) {
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        event.setY(0.35);
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        event.setY(-0.35);
                    } else {
                        event.setY(0);
                    }

                    mc.thePlayer.motionY = 0;
                } else {
                    if (mc.thePlayer.onGround && MovementUtil.isMoving()) {
                        MovementUtil.jump(event);
                    }
                }

                if (!started) {
                    speed = 0.65;
                    started = true;
                } else {
                    if (clientSpeed.is("Normal")) {
                        double baseSpeed = 0.33 + MovementUtil.getSpeedAmplifier() * 0.02;

                        if (mc.thePlayer.onGround) {
                            speed = 0.33 + baseSpeed;
                        } else {
                            speed = Math.min(speed - baseSpeed * distance * 0.15, baseSpeed);
                        }

                        speed = Math.max(speed, 0.2);
                    } else if (clientSpeed.is("Custom")) {
                        //speed = Math.max(customClientSpeed.getValue() - distance * customClientSpeed.getValue() * 0.15, 0.3);
                        speed = customClientSpeed.getValue();
                    }
                }

                MovementUtil.strafe(event, speed);

                lastDirection = direction;
                break;
            case "KKC": {
                MovementUtil.strafe(event);
            }
        }
    }

    @Listener
    public void onEntityAction(EntityActionEvent event) {
        switch (mode.getValue()) {
            case "Fake strafe":
                lastActualX = actualX;
                lastActualY = actualY;
                lastActualZ = actualZ;

                float direction = RotationsUtil.getRotationsToPosition(lastActualX, lastActualY, lastActualZ, mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)[0];

                float gcd = RotationsUtil.getGCD();

                float yawDiff = (direction - lastYaw);

                float fixedYawDiff = yawDiff - (yawDiff % gcd);

                direction = lastYaw + fixedYawDiff;

                float dir = direction * 0.017453292F;

                float friction = getFriction(actualX, actualY, actualZ) * 0.91F;

                if (actualGround) {
                    motionY = (double) mc.thePlayer.getJumpUpwardsMotion();

                    if (mc.thePlayer.isPotionActive(Potion.jump)) {
                        motionY += (double) ((float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                    }

                    if (!wasCollided) {
                        motionX -= (double) (MathHelper.sin(dir) * 0.2F);
                        motionZ += (double) (MathHelper.cos(dir) * 0.2F);
                    }
                }

                float aa = 0.16277136F / (friction * friction * friction);

                float attributeSpeed;

                mc.thePlayer.setSprinting(!wasCollided);

                if (actualGround) {
                    attributeSpeed = mc.thePlayer.getAIMoveSpeed() * aa;
                } else {
                    attributeSpeed = wasCollided ? 0.02F : 0.026F;
                }

                boolean oldActualGround = actualGround;

                float forward = 0.98F;
                float strafe = 0F;

                float thing = strafe * strafe + forward * forward;

                if (thing >= 1.0E-4F) {
                    thing = MathHelper.sqrt_float(thing);

                    if (thing < 1.0F) {
                        thing = 1.0F;
                    }

                    thing = attributeSpeed / thing;
                    strafe = strafe * thing;
                    forward = forward * thing;
                    float f1 = MathHelper.sin(direction * (float) Math.PI / 180.0F);
                    float f2 = MathHelper.cos(direction * (float) Math.PI / 180.0F);
                    motionX += (double) (strafe * f2 - forward * f1);
                    motionZ += (double) (forward * f2 + strafe * f1);
                }

                if (groundStrafe.getValue() && actualGround) {
                    double speed = Math.hypot(motionX, motionZ);

                    motionX = -Math.sin(Math.toRadians(direction)) * speed;
                    motionZ = Math.cos(Math.toRadians(direction)) * speed;
                }

                double clientX = mc.thePlayer.posX;
                double clientY = mc.thePlayer.posY;
                double clientZ = mc.thePlayer.posZ;

                double clientMotionX = mc.thePlayer.motionX;
                double clientMotionY = mc.thePlayer.motionY;
                double clientMotionZ = mc.thePlayer.motionZ;

                boolean clientGround = mc.thePlayer.onGround;

                mc.thePlayer.setPosition(actualX, actualY, actualZ);

                mc.thePlayer.onGround = actualGround;

                mc.thePlayer.moveEntityNoEvent(motionX, motionY, motionZ);

                boolean collided = mc.thePlayer.isCollidedHorizontally;

                motionX = mc.thePlayer.posX - lastActualX;
                motionY = mc.thePlayer.posY - lastActualY;
                motionZ = mc.thePlayer.posZ - lastActualZ;

                actualX = mc.thePlayer.posX;
                actualY = mc.thePlayer.posY;
                actualZ = mc.thePlayer.posZ;

                actualGround = mc.thePlayer.onGround;

                mc.thePlayer.setPosition(clientX, clientY, clientZ);
                mc.thePlayer.onGround = clientGround;

                mc.thePlayer.motionX = clientMotionX;
                mc.thePlayer.motionY = clientMotionY;
                mc.thePlayer.motionZ = clientMotionZ;

                if (oldActualGround) {
                    motionX *= friction * 0.91F;
                    motionZ *= friction * 0.91F;
                } else {
                    motionX *= 0.91F;
                    motionZ *= 0.91F;
                }

                motionY -= 0.08;
                this.motionY *= 0.9800000190734863D;

                if (Math.abs(motionX) < 0.005) {
                    motionX = 0;
                }

                if (Math.abs(motionY) < 0.005) {
                    motionY = 0;
                }

                if (Math.abs(motionZ) < 0.005) {
                    motionZ = 0;
                }

                if (sprint.getValue()) {
                    event.setSprinting(!wasCollided);
                } else {
                    event.setSprinting(false);
                }

                mc.thePlayer.setSprinting(true);

                event.setSneaking(false);

                wasCollided = collided;
                break;
            case "Test":
            case "Test2":
                event.setSprinting(MovementUtil.isMoving());
                break;
        }
    }

    @Listener
    public void onMotion(MotionEvent event) {
        switch (mode.getValue()) {
            case "Fake strafe":
                event.setX(actualX);
                event.setY(actualY);
                event.setZ(actualZ);
                event.setOnGround(actualGround);

                float direction = RotationsUtil.getRotationsToPosition(lastActualX, lastActualY, lastActualZ, mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)[0];

                if (rotate.getValue()) {
                    final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
                    final float gcd = f * f * f * 1.2F;

                    final float deltaYaw = direction - lastYaw;

                    final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);

                    direction = lastYaw + fixedDeltaYaw;

                    lastYaw = direction;

                    event.setYaw(direction);
                }
                break;
            case "MMC":
                event.setYaw(event.getYaw() - 180);

                if (started) {
                    event.setPitch(counter % 2 == 0 || !fullScaffold.getValue() ? -82 : 82);
                }
                break;
            case "KKC":
                mc.thePlayer.motionY -= 0.00348;
                mc.thePlayer.jumpMovementFactor = 0.026f;
                mc.gameSettings.keyBindJump.setPressed(GameSettings.isKeyDown(mc.gameSettings.keyBindJump));
                if (MovementUtil.isMoving() && mc.thePlayer.onGround) {
                    mc.gameSettings.keyBindJump.setPressed(false);
                    mc.thePlayer.jump();
                    MovementUtil.strafe();
                } else if (MovementUtil.getSpeedAmplifier() < 0.215) {
                    MovementUtil.strafe(0.215f);
                }
                break;
        }

        takingVelocity = false;

    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        switch (mode.getValue()) {
            case "Fake strafe":
                if (renderRealPosBox.getValue() && mc.gameSettings.thirdPersonView > 0) {
                    RenderUtil.prepareBoxRender(3.25F, 1F, 1F, 1F, 0.8F);

                    RenderUtil.renderCustomPlayerBox(mc.getRenderManager(), event.getPartialTicks(), actualX, actualY, actualZ, lastActualX, lastActualY, lastActualZ);

                    RenderUtil.stopBoxRender();
                }
                break;
        }
    }

    @Listener
    public void onReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = event.getPacket();

            if (mc.thePlayer.getEntityId() == packet.getEntityID()) {
                takingVelocity = true;

                double velocityX = packet.getMotionX() / 8000.0D;
                double velocityY = packet.getMotionY() / 8000.0D;
                double velocityZ = packet.getMotionZ() / 8000.0D;

                velocityDist = Math.hypot(velocityX, velocityZ);

                if (mode.is("Fake strafe")) {
                    event.setCancelled(true);

                    switch (velocityMode.getValue()) {
                        case "Vertical":
                            motionY = velocityY;
                            break;
                        case "Legit":
                            motionX = velocityX;
                            motionY = velocityY;
                            motionZ = velocityZ;
                            break;
                    }
                }
            }
        } else if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (mode.is("Fake strafe")) {
                this.setEnabled(false);
            }
        }
    }

    private float getFriction(double x, double y, double z) {
        Block block = mc.theWorld.getBlockState(new BlockPos(x, Math.floor(y) - 1, z)).getBlock();

        if (block != null) {
            if (block instanceof BlockIce || block instanceof BlockPackedIce) {
                return 0.98F;
            } else if (block instanceof BlockSlime) {
                return 0.8F;
            }
        }

        return 0.6F;
    }

    @Override
    public String getSuffix() {
        if (mode.is("Watchdog")) {
            return mode.getValue() + " (" + watchdogMode.getValue() + ")";
        }

        return mode.getValue();
    }

}
