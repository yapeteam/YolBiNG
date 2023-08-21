package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.event.impl.MotionEvent;
import cn.yapeteam.yolbi.event.impl.PacketReceiveEvent;
import cn.yapeteam.yolbi.event.impl.TickEvent;
import cn.yapeteam.yolbi.module.impl.movement.Fly;
import cn.yapeteam.yolbi.module.impl.movement.Longjump;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.misc.LogUtil;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import lombok.Getter;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class AntiVoid extends Module {

    private final ModeValue<String> mode = new ModeValue<>("Mode", "Flag", "Flag", "Collision flag", "Blink", "Bounce");

    private final BooleanValue stopHorizontalMove = new BooleanValue("Stop horizontal move", () -> mode.is("Blink"), false);

    private final NumberValue<Double> bounceMotion = new NumberValue<>("Bounce motion", () -> mode.is("Bounce"), 1.5, 0.4, 3.0, 0.1);

    private final NumberValue<Double> minFallDist = new NumberValue<>("Min fall dist", 3.5, 2.0, 10.0, 0.25);

    private PlayerInfo lastSafePos;

    private BlockPos collisionBlock;

    @Getter
    private boolean blinking;

    private Fly flyModule;
    private Longjump longjumpModule;

    private boolean receivedLagback;

    public AntiVoid() {
        super("AntiVoid", ModuleCategory.PLAYER);
        this.addValues(mode, stopHorizontalMove, bounceMotion, minFallDist);
    }

    @Override
    public void onEnable() {
        collisionBlock = null;

        lastSafePos = new PlayerInfo(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround, mc.thePlayer.fallDistance, mc.thePlayer.inventory.currentItem);
    }

    @Override
    public void onDisable() {
        if(blinking) {
            Vestige.instance.getPacketBlinkHandler().stopAll();
            LogUtil.addChatMessage("Stopped blinking");
            blinking = false;
        }

        receivedLagback = false;
    }

    @Override
    public void onClientStarted() {
        flyModule = Vestige.instance.getModuleManager().getModule(Fly.class);
        longjumpModule = Vestige.instance.getModuleManager().getModule(Longjump.class);
    }

    @Listener(Priority.HIGHER)
    public void onTick(TickEvent event) {
        if(mc.thePlayer.ticksExisted < 10) {
            collisionBlock = null;
            return;
        }

        switch (mode.getValue()) {
            case "Bounce":
                if(shouldSetback() && mc.thePlayer.motionY < -0.1) {
                    mc.thePlayer.motionY = bounceMotion.getValue();
                }
                break;
            case "Collision flag":
                if(shouldSetback()) {
                    if(collisionBlock != null) {
                        mc.theWorld.setBlockToAir(collisionBlock);
                    }

                    collisionBlock = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);

                    mc.theWorld.setBlockState(collisionBlock, Blocks.barrier.getDefaultState());
                } else {
                    if(collisionBlock != null) {
                        mc.theWorld.setBlockToAir(collisionBlock);
                        collisionBlock = null;
                    }
                }
                break;
            case "Blink":
                if(isSafe()) {
                    lastSafePos = new PlayerInfo(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround, mc.thePlayer.fallDistance, mc.thePlayer.inventory.currentItem);

                    receivedLagback = false;

                    if(blinking) {
                        Vestige.instance.getPacketBlinkHandler().stopAll();
                        //LogUtil.addChatMessage("Antivoid : Stopped blinking");
                        blinking = false;
                    }
                } else if(!receivedLagback) {
                    if(shouldSetback()) {
                        if(blinking) {
                            mc.thePlayer.setPosition(lastSafePos.x, lastSafePos.y, lastSafePos.z);

                            if(stopHorizontalMove.getValue()) {
                                mc.thePlayer.motionX = 0;
                                mc.thePlayer.motionZ = 0;
                            } else {
                                mc.thePlayer.motionX = lastSafePos.motionX;
                                mc.thePlayer.motionZ = lastSafePos.motionZ;
                            }

                            mc.thePlayer.motionY = lastSafePos.motionY;

                            mc.thePlayer.rotationYaw = lastSafePos.yaw;
                            mc.thePlayer.rotationPitch = lastSafePos.pitch;

                            mc.thePlayer.onGround = lastSafePos.onGround;

                            mc.thePlayer.fallDistance = lastSafePos.fallDist;

                            mc.thePlayer.inventory.currentItem = lastSafePos.itemSlot;
                            mc.playerController.currentPlayerItem = lastSafePos.itemSlot;

                            Vestige.instance.getPacketBlinkHandler().releasePing();
                            Vestige.instance.getPacketBlinkHandler().clearMove();
                            Vestige.instance.getPacketBlinkHandler().clearOther();

                            LogUtil.addChatMessage("Antivoid : Set back");
                        }
                    } else {
                        if(!blinking) {
                            Vestige.instance.getPacketBlinkHandler().startBlinkingAll();
                            //LogUtil.addChatMessage("Antivoid : Started blinking");
                            blinking = true;
                        }
                    }
                }
                break;
        }
    }

    @Listener(Priority.LOWER)
    public void onMotion(MotionEvent event) {
        switch (mode.getValue()) {
            case "Flag":
                if(shouldSetback()) {
                    event.setY(event.getY() + 8 + Math.random());
                }
                break;
        }
    }

    @Listener
    public void onReceive(PacketReceiveEvent event) {
        if(event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = event.getPacket();

            if(mode.is("Blink") && blinking) {
                mc.thePlayer.onGround = false;

                mc.thePlayer.fallDistance = lastSafePos.fallDist;

                mc.thePlayer.inventory.currentItem = lastSafePos.itemSlot;
                mc.playerController.currentPlayerItem = lastSafePos.itemSlot;

                Vestige.instance.getPacketBlinkHandler().releasePing();
                Vestige.instance.getPacketBlinkHandler().clearMove();
                Vestige.instance.getPacketBlinkHandler().clearOther();

                Vestige.instance.getPacketBlinkHandler().stopAll();

                lastSafePos = new PlayerInfo(packet.getX(), packet.getY(), packet.getZ(), 0, 0, 0, packet.getYaw(), packet.getPitch(), false, mc.thePlayer.fallDistance, mc.thePlayer.inventory.currentItem);

                LogUtil.addChatMessage("Antivoid : Received lagback");

                blinking = false;

                receivedLagback = true;
            }
        }
    }

    private boolean shouldSetback() {
        return mc.thePlayer.fallDistance >= minFallDist.getValue() && !WorldUtil.isBlockUnder() && mc.thePlayer.ticksExisted >= 100;
    }

    private boolean isSafe() {
        return WorldUtil.isBlockUnder() || !mc.getNetHandler().doneLoadingTerrain || mc.thePlayer.ticksExisted < 100 || flyModule.isEnabled() || longjumpModule.isEnabled();
    }

    private class PlayerInfo {
        private final double x, y, z;
        private final double motionX, motionY, motionZ;
        private final float yaw, pitch;
        private final boolean onGround;
        private final float fallDist;
        private final int itemSlot;

        private PlayerInfo(double x, double y, double z, double motionX, double motionY, double motionZ, float yaw, float pitch, boolean onGround, float fallDist, int itemSlot) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.yaw = yaw;
            this.pitch = pitch;
            this.onGround = onGround;
            this.fallDist = fallDist;
            this.itemSlot = itemSlot;
        }
    }

}
