package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
import cn.yapeteam.yolbi.event.impl.player.EventEntityAction;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventMove;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.player.MovementUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "LongJump", category = ModuleCategory.MOVEMENT)
public class LongJump extends Module {

    public final ModeValue<String> mode = new ModeValue<>("Mode", "Vanilla", "Vanilla", "Hycraft", "Self damage");

    private final NumberValue<Double> motionY = new NumberValue<>("Motion Y", () -> mode.is("Vanilla"), 0.4, 0.1, 9.0, 0.1);
    private final NumberValue<Double> speed = new NumberValue<>("Speed", () -> mode.is("Vanilla"), 1.0, 0.1, 9.0, 0.1);

    private final BooleanValue stopMovement = new BooleanValue("Stop movement", () -> mode.is("Self damage"), false);
    private final NumberValue<Integer> waitingTicks = new NumberValue<>("Waiting ticks", () -> mode.is("Self damage"), 10, 4, 20, 1);
    private final ModeValue<String> horizontalMove = new ModeValue<>("Horizontal move", () -> mode.is("Self damage"), "Ignore", "Legit", "Ignore", "Boost", "Verus");

    private final NumberValue<Double> horizontalBoostAmount = new NumberValue<>("Horizontal boost amount", () -> mode.is("Self damage") && horizontalMove.is("Boost"), 0.2, 0.02, 1.0, 0.02);

    private final NumberValue<Double> afterVelocityYBoost = new NumberValue<>("After velocity Y boost", () -> mode.is("Self damage"), 0.0, 0.0, 0.08, 0.002);

    private boolean started;
    private int counter, ticks;

    private double velocityX, velocityY, velocityZ;

    public LongJump() {
        this.addValues(mode, motionY, speed, stopMovement, waitingTicks, horizontalMove, horizontalBoostAmount, afterVelocityYBoost);
    }

    @Override
    public void onEnable() {
        counter = ticks = 0;
        started = false;

        velocityY = -1;

        switch (mode.getValue()) {
            case "Self damage":
                if (!mc.thePlayer.onGround) {
                    counter = 3;
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        YolBi.instance.getPacketBlinkHandler().stopAll();
        mc.timer.timerSpeed = 1F;

        switch (mode.getValue()) {
            case "Vanilla":
                MovementUtil.strafe(0.1);
                break;
        }
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        switch (mode.getValue()) {
            case "Self damage":
                if (started) {
                    ++ticks;

                    if (ticks == waitingTicks.getValue() && velocityY != -1) {
                        switch (horizontalMove.getValue()) {
                            case "Legit":
                                mc.thePlayer.motionX = velocityX;
                                mc.thePlayer.motionZ = velocityZ;
                                break;
                            case "Ignore":
                                break;
                            case "Boost":
                                mc.thePlayer.motionX -= (double) (MathHelper.sin((float) Math.toRadians(mc.thePlayer.rotationYaw)) * horizontalBoostAmount.getValue());
                                mc.thePlayer.motionZ += (double) (MathHelper.cos((float) Math.toRadians(mc.thePlayer.rotationYaw)) * horizontalBoostAmount.getValue());
                                break;
                        }

                        mc.thePlayer.motionY = velocityY;
                    }

                    if (ticks > waitingTicks.getValue() && velocityY != -1) {
                        mc.thePlayer.motionY += afterVelocityYBoost.getValue();
                    }

                    if (ticks > 2 && ticks < waitingTicks.getValue()) {
                        YolBi.instance.getPacketBlinkHandler().startBlinkingAll();
                    } else {
                        YolBi.instance.getPacketBlinkHandler().stopAll();
                    }

                    if (mc.thePlayer.onGround) {
                        this.setEnabled(false);
                    }
                } else {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        counter++;

                        if (counter > 3) {
                            started = true;
                            mc.gameSettings.keyBindForward.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
                            return;
                        }
                    }

                    if (mc.thePlayer.motionY > 0.3) {
                        YolBi.instance.getPacketBlinkHandler().stopAll();
                    } else {
                        YolBi.instance.getPacketBlinkHandler().startBlinkingAll();
                    }
                }
                break;
            case "Hycraft":
                if (mc.thePlayer.onGround) {
                    if (started) {
                        this.setEnabled(false);
                        return;
                    }

                    if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.jump();
                        started = true;
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
    public void onEntityAction(EventEntityAction event) {
        if (mode.is("Self damage") && stopMovement.getValue()) {
            if (counter < 4) {
                event.setSprinting(false);
            }
        }
    }

    @Listener
    public void onMove(EventMove event) {
        switch (mode.getValue()) {
            case "Vanilla":
                if (mc.thePlayer.onGround) {
                    if (started) {
                        this.setEnabled(false);
                        return;
                    }

                    if (MovementUtil.isMoving()) {
                        started = true;
                        if (motionY.getValue() == 0.4) {
                            event.setY((double) 0.42F);
                        } else {
                            event.setY(mc.thePlayer.motionY = motionY.getValue());
                        }
                    }
                }

                MovementUtil.strafe(event, speed.getValue());
                break;
            case "Self damage":
                if (stopMovement.getValue()) {
                    if (counter <= 3) {
                        MovementUtil.strafe(event, 0);
                    } else if (counter == 4 && mc.thePlayer.onGround && event.getY() > 0.4) {
                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                            MovementUtil.strafe(event, 0.6 + MovementUtil.getSpeedAmplifier() * 0.07);
                        } else {
                            MovementUtil.strafe(event, 0.6);
                        }
                    }
                }

                if (started && horizontalMove.is("Verus")) {
                    if (ticks >= waitingTicks.getValue() + 14) {
                        MovementUtil.strafe(event, 0.28);
                    } else if (ticks >= waitingTicks.getValue()) {
                        MovementUtil.strafe(event, 9);
                    }
                }
                break;
        }
    }

    @Listener
    public void onMotion(EventMotion event) {
        switch (mode.getValue()) {
            case "Self damage":
                if (!started && counter < 3) {
                    event.setOnGround(false);
                }
                break;
        }
    }

    @Listener
    public void onReceive(EventPacketReceive event) {
        switch (mode.getValue()) {
            case "Self damage":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = event.getPacket();

                    if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        event.setCancelled(true);
                        velocityX = packet.getMotionX() / 8000.0D;
                        velocityY = packet.getMotionY() / 8000.0D;
                        velocityZ = packet.getMotionZ() / 8000.0D;
                    }
                }
                break;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

}
