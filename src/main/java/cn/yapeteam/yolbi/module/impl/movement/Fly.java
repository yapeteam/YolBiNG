package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.block.EventBlockBB;
import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.event.impl.player.*;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.misc.TimerUtil;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import cn.yapeteam.yolbi.util.player.MovementUtil;
import cn.yapeteam.yolbi.util.player.PlayerUtil;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
@ModuleInfo(name = "Fly", category = ModuleCategory.MOVEMENT)
public class Fly extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Vanilla", "Vanilla", "Collision", "NCP", "Blocksmc", "Velocity","VerusCustom");

    private final ModeValue<String> vanillaMode = new ModeValue<>("Vanilla Mode", () -> mode.is("Vanilla"), "Motion", "Motion", "Creative");
    private final NumberValue<Double> vanillaSpeed = new NumberValue<>("Vanilla speed", () -> mode.is("Vanilla") && vanillaMode.is("Motion"), 2.0, 0.2, 9.0, 0.2);

    private final NumberValue<Double> groundSpeedValue = new NumberValue<>("GroundSpeed", () -> mode.is("VerusCustom") , 2.0, 0.1, 9.0, 0.1);
    private final NumberValue<Double> airSpeedValue = new NumberValue<>("AirSpeed", () -> mode.is("VerusCustom") , 2.0, 0.1, 9.0, 0.1);
    private final NumberValue<Integer> hopDelayValue = new NumberValue<>("HopDelay", () -> mode.is("VerusCustom") , 10, 1, 20, 1);

    private final NumberValue<Double> vanillaVerticalSpeed = new NumberValue<>("Vanilla vertical speed", () -> mode.is("Vanilla") && vanillaMode.is("Motion"), 2.0, 0.2, 9.0, 0.2);

    private final ModeValue<String> collisionMode = new ModeValue<>("Collision mode", () -> mode.is("Collision"), "Airwalk", "Airwalk", "Airjump");

    private final ModeValue<String> ncpMode = new ModeValue<>("NCP Mode", () -> mode.is("NCP"), "Old", "Old");
    private final NumberValue<Double> ncpSpeed = new NumberValue<>("NCP speed", () -> mode.is("NCP") && ncpMode.is("Old"), 1.0, 0.3, 1.7, 0.05);
    private final BooleanValue damage = new BooleanValue("Damage", () -> mode.is("NCP") && ncpMode.is("Old"), false);
    private final BooleanValue DJC = new BooleanValue("DJCAutoDisable", () -> mode.is("NCP") && ncpMode.is("Old"), false);
    private final NumberValue<Float> Timer = new NumberValue<>("NCP Timer", () -> mode.is("NCP") && ncpMode.is("Old"), 1.0f, 0.1f, 5.0f, 0.05f);

    private final ModeValue<String> velocityMode = new ModeValue<>("Velocity Mode", () -> mode.is("Velocity"), "Bow", "Bow", "Bow2", "Wait for hit");

    private final BooleanValue automated = new BooleanValue("Automated", () -> mode.is("Blocksmc"), false);

    private double speed;

    private boolean takingVelocity;

    private double velocityX, velocityY, velocityZ;

    private int counter, ticks;

    private boolean started;

    private double lastMotionX, lastMotionY, lastMotionZ;

    private boolean notMoving;

    private float lastYaw;
    private int waitTicks; //VerusCustom
    private double launchY;

    private BlockPos lastBarrier;
    private final TimerUtil DJCTimer = new TimerUtil();

    public Fly() {
        this.addValues(mode, vanillaMode, vanillaSpeed, vanillaVerticalSpeed, ncpMode, ncpSpeed, damage, DJC, Timer, velocityMode, automated
        ,groundSpeedValue,airSpeedValue,hopDelayValue);
    }

    @Override
    public void onEnable() {
        launchY = mc.thePlayer.posY;
        counter = ticks = 0;
        waitTicks = 0;
        started = false;

        notMoving = false;

        lastMotionX = mc.thePlayer.motionX;
        lastMotionY = mc.thePlayer.motionY;
        lastMotionZ = mc.thePlayer.motionZ;

        lastYaw = mc.thePlayer.rotationYaw;

        lastBarrier = null;
        DJCTimer.reset();
        switch (mode.getValue()) {
            case "NCP":
                if (ncpMode.is("Old")) {
                    if (mc.thePlayer.onGround) {
                        speed = ncpSpeed.getValue();

                        if (damage.getValue()) {
                            PlayerUtil.ncpDamage();
                        }
                    } else {
                        speed = 0.28;
                    }
                }
                break;
            case "Velocity":
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.capabilities.isFlying = false;
        mc.timer.timerSpeed = 1;
        YolBi.instance.getPacketBlinkHandler().stopAll();

        switch (mode.getValue()) {
            case "Vanilla":
                if (vanillaMode.is("Motion")) {
                    MovementUtil.strafe(0);
                }
                break;
            case "NCP":
                if (ncpMode.is("Old")) {
                    MovementUtil.strafe(0);
                }
                break;
            case "Velocity":
                switch (velocityMode.getValue()) {
                    case "Wait for hit":
                        mc.thePlayer.motionX = lastMotionX * 0.91;
                        mc.thePlayer.motionY = lastMotionY;
                        mc.thePlayer.motionZ = lastMotionZ * 0.91;
                        break;
                    case "Bow":
                        mc.thePlayer.rotationYaw = lastYaw;
                        mc.thePlayer.rotationPitch = -90;

                        mc.gameSettings.keyBindUseItem.setPressed(false);
                        break;
                    case "Bow2":
                        mc.thePlayer.motionX = lastMotionX * 0.91;
                        mc.thePlayer.motionY = lastMotionY;
                        mc.thePlayer.motionZ = lastMotionZ * 0.91;

                        mc.thePlayer.rotationPitch = -90;

                        mc.gameSettings.keyBindUseItem.setPressed(false);
                        break;
                }
                break;
            case "Blocksmc":
                MovementUtil.strafe(0);
                break;
        }

        if (lastBarrier != null) {
            mc.theWorld.setBlockToAir(lastBarrier);
        }

        mc.timer.timerSpeed = 1F;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        switch (mode.getValue()) {
            case "Velocity":
                switch (velocityMode.getValue()) {
                    case "Bow":
                        if (takingVelocity) {
                            YolBi.instance.getPacketBlinkHandler().stopAll();

                            mc.thePlayer.motionY = velocityY;

                            boolean sameXDir = lastMotionX > 0.01 && velocityX > 0 || lastMotionX < -0.01 && velocityX < 0;
                            boolean sameZDir = lastMotionZ > 0.01 && velocityZ > 0 || lastMotionZ < -0.01 && velocityZ < 0;

                            if (sameXDir && sameZDir) {
                                mc.thePlayer.motionX = velocityX;
                                mc.thePlayer.motionZ = velocityZ;
                            }
                        }
                        break;
                }
                break;
            case "Collision":
                switch (collisionMode.getValue()) {
                    case "Airwalk":
                        mc.thePlayer.onGround = true;
                        break;
                    case "Airjump":
                        if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.thePlayer.jump();
                        }

                        if (mc.thePlayer.fallDistance > (mc.gameSettings.keyBindJump.isKeyDown() ? 0 : 0.7)) {
                            if (lastBarrier != null) {
                                mc.theWorld.setBlockToAir(lastBarrier);
                            }

                            lastBarrier = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);

                            mc.theWorld.setBlockState(lastBarrier, Blocks.barrier.getDefaultState());
                        }
                        break;
                }
                break;
            case "Test":
                if (mc.thePlayer.onGround) {
                    if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.jump();
                    }
                } else {
                    if (ticks >= 2 && ticks <= 8) {
                        mc.thePlayer.motionY += 0.07;
                    }

                    ticks++;
                }
                break;
        }
    }

    @Listener
    public void onMove(EventMove event) {
        switch (mode.getValue()) {
            case "Vanilla":
                switch (vanillaMode.getValue()) {
                    case "Motion":
                        MovementUtil.strafe(event, vanillaSpeed.getValue());

                        if (mc.gameSettings.keyBindJump.isKeyDown()) {
                            event.setY(vanillaVerticalSpeed.getValue());
                        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                            event.setY(-vanillaVerticalSpeed.getValue());
                        } else {
                            event.setY(0);
                        }

                        mc.thePlayer.motionY = 0;
                        break;
                    case "Creative":
                        mc.thePlayer.capabilities.isFlying = true;
                        break;
                }
                break;
            case "Collision":
                if (collisionMode.is("Airwalk")) {
                    event.setY(mc.thePlayer.motionY = 0);
                }
                break;
            case "NCP":
                switch (ncpMode.getValue()) {
                    case "Old":
                        if (mc.thePlayer.onGround) {
                            MovementUtil.jump(event);
                            MovementUtil.strafe(event, 0.58);
                        } else {
                            event.setY(mc.thePlayer.motionY = 1E-10);

                            if (!MovementUtil.isMoving() || mc.thePlayer.isCollidedHorizontally || speed < 0.28) {
                                speed = 0.28;
                            }

                            MovementUtil.strafe(event, speed);

                            speed -= speed / 159;
                            mc.timer.timerSpeed = Timer.getValue();
                            if (DJC.getValue() && DJCTimer.delay((long) (2500L / Timer.getValue()))) {
                                DJCTimer.reset();
                                this.setEnabled(false);
                            }
                        }
                        break;
                }
                break;
            case "Velocity":
                switch (velocityMode.getValue()) {
                    case "Wait for hit":
                        if (takingVelocity) {
                            event.setY(mc.thePlayer.motionY = velocityY);

                            event.setX(mc.thePlayer.motionX = lastMotionX);
                            event.setZ(mc.thePlayer.motionZ = lastMotionZ);

                            notMoving = false;

                            ticks = 0;
                        } else {
                            if (event.getY() < -0.3 && !notMoving) {
                                lastMotionX = event.getX();
                                lastMotionY = event.getY();
                                lastMotionZ = event.getZ();

                                notMoving = true;
                            }

                            if (notMoving) {
                                event.setY(mc.thePlayer.motionY = 0);
                                MovementUtil.strafe(event, 0);
                            }

                            ticks++;
                        }
                        break;
                    case "Bow":
                        for (int i = 8; i >= 0; i--) {
                            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                            if (stack != null && stack.getItem() instanceof ItemBow) {
                                mc.thePlayer.inventory.currentItem = i;
                                break;
                            }
                        }

                        if (takingVelocity) {
                            mc.timer.timerSpeed = 1F;

                            notMoving = false;

                            ticks = 0;
                            counter = 0;

                            started = true;
                        } else {
                            if (ticks <= 3) {
                                if (started) {
                                    mc.timer.timerSpeed = 1.5F;
                                }
                                mc.gameSettings.keyBindUseItem.setPressed(true);
                            } else {
                                mc.gameSettings.keyBindUseItem.setPressed(false);
                            }

                            ticks++;
                        }

                        if (ticks >= 6) {
                            mc.timer.timerSpeed = 0.03F;
                        } else if (ticks == 5) {
                            mc.timer.timerSpeed = 0.1F;
                        }

                        if (started && !notMoving && !takingVelocity && MovementUtil.getHorizontalMotion() > 0.07) {
                            //event.setY(mc.thePlayer.motionY = event.getY() + 0.01);
                        }
                        break;
                    case "Bow2":
                        for (int i = 8; i >= 0; i--) {
                            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                            if (stack != null && stack.getItem() instanceof ItemBow) {
                                mc.thePlayer.inventory.currentItem = i;
                                break;
                            }
                        }

                        if (takingVelocity) {
                            event.setY(mc.thePlayer.motionY = velocityY);

                            boolean sameXDir = lastMotionX > 0 && velocityX > 0 || lastMotionX < 0 && velocityX < 0;
                            boolean sameZDir = lastMotionZ > 0 && velocityZ > 0 || lastMotionZ < 0 && velocityZ < 0;

                            if (sameXDir && sameZDir) {
                                event.setX(mc.thePlayer.motionX = velocityX);
                                event.setZ(mc.thePlayer.motionZ = velocityZ);
                            } else {
                                event.setX(mc.thePlayer.motionX = lastMotionX);
                                event.setZ(mc.thePlayer.motionZ = lastMotionZ);
                            }

                            notMoving = false;

                            ticks = 0;
                        } else {
                            if (ticks >= 6 && !notMoving) {
                                lastMotionX = event.getX();
                                lastMotionY = event.getY();
                                lastMotionZ = event.getZ();

                                notMoving = true;
                            }

                            if (ticks >= 1 && ticks <= 6) {
                                mc.gameSettings.keyBindUseItem.setPressed(true);
                            } else {
                                mc.gameSettings.keyBindUseItem.setPressed(false);
                            }

                            if (notMoving) {
                                event.setY(mc.thePlayer.motionY = 0);
                                MovementUtil.strafe(event, 0);
                            }

                            ticks++;
                        }
                        break;
                }
                break;
            case "Blocksmc":
                if (automated.getValue()) {
                    if (++counter < 6) {
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

                        switch (counter) {
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
                                break;
                        }

                        PacketUtil.sendPacket(new C0APacketAnimation());

                        MovementUtil.strafe(event, 0.04);
                        return;
                    }
                }

                BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ);

                if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) {
                    started = true;
                }

                YolBi.instance.getPacketBlinkHandler().startBlinkingAll();

                if (started) {
                    mc.timer.timerSpeed = 0.3F;

                    if (mc.thePlayer.onGround) {
                        if (ticks > 0) {
                            this.setEnabled(false);
                            return;
                        }

                        if (MovementUtil.isMoving()) {
                            MovementUtil.jump(event);
                            MovementUtil.strafe(event, 0.58);
                        }
                    } else if (ticks == 1) {
                        MovementUtil.strafe(event, 9.5);
                    }

                    ticks++;
                } else {
                    MovementUtil.strafe(event, 0.1);
                }
                break;
            case "VerusCustom":{
                if (MovementUtil.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        MovementUtil.strafe(groundSpeedValue.getValue());
                        waitTicks++;
                        if (waitTicks >= hopDelayValue.getValue()) {
                            waitTicks = 0;
                            mc.thePlayer.triggerAchievement(StatList.jumpStat);
                            mc.thePlayer.motionY = 0.0;
                            event.setY(0.41999998688698);
                        }
                    } else {
                        MovementUtil.strafe(airSpeedValue.getValue());
                    }
                }

                break;
            }
        }

        takingVelocity = false;
    }

    @Listener
    public void onEntityAction(EventEntityAction event) {
        switch (mode.getValue()) {
            case "Velocity":
                if (velocityMode.is("Wait for hit")) {
                    event.setSprinting(true);
                } else if (velocityMode.is("Airjump")) {
                    if (!started) {
                        event.setSprinting(false);
                    }
                }
                break;
            case "Blocksmc":
                if (automated.getValue() && counter < 6) {
                    event.setSprinting(false);
                }
                break;
        }
    }

    @Listener
    public void onMotion(EventMotion event) {
        switch (mode.getValue()) {
            case "Velocity":
                if (velocityMode.is("Bow") || velocityMode.is("Bow2")) {
                    event.setPitch(-90);
                }
                break;
            case "Collision":
                if (collisionMode.is("Airwalk")) {
                    event.setOnGround(true);
                }
                break;
        }
    }

    @Listener
    public void onPostMotion(EventPostMotion event) {

    }
    @Listener
    public void onBlockBB(EventBlockBB event){
        if (mode.is("VerusCustom")){
            if (event.getBlock() instanceof BlockAir && event.getY() <= launchY){
                event.setAxisAlignedBB(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1.0, launchY, event.getZ() + 1.0));

            }
        }
    }

    @Listener
    public void onReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = event.getPacket();

            if (mc.thePlayer.getEntityId() == packet.getEntityID()) {
                takingVelocity = true;

                velocityX = packet.getMotionX() / 8000.0;
                velocityY = packet.getMotionY() / 8000.0;
                velocityZ = packet.getMotionZ() / 8000.0;

                if (mode.is("Velocity")) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (mode.is("Velocity")) {
                this.setEnabled(false);
            }
        }
    }

    @Listener
    public void onSend(EventPacketSend event) {
        switch (mode.getValue()) {
            case "Velocity":
                if (velocityMode.is("Wait for hit") || velocityMode.is("Bow2")) {
                    if (event.getPacket() instanceof C03PacketPlayer && notMoving) {
                        event.setCancelled(true);
                    }
                }
                break;
        }
    }
//    @Listener
//    public void onRender(RenderEvent event){
//        if (mode.is("NCP")) {
//            if (ncpMode.is("Old")) {
//                if (DJC.getValue()){
//                    //System.out.println(DJCTimer.getTimeElapsed());
//                    YolBi.instance.getFontManager().getPingFang18().drawCenteredString(String.valueOf(DJCTimer.getTimeElapsed()),
//                            100, 100, -1);
//                }
//            }
//        }
//    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }
}
