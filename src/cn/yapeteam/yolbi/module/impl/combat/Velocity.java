package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.network.PacketReceiveEvent;
import cn.yapeteam.yolbi.event.impl.player.PostMotionEvent;
import cn.yapeteam.yolbi.event.impl.player.UpdateEvent;
import cn.yapeteam.yolbi.module.impl.movement.Blink;
import cn.yapeteam.yolbi.module.impl.movement.Longjump;
import cn.yapeteam.yolbi.module.impl.movement.Speed;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.player.KeyboardUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Velocity extends Module {

    public final ModeValue<String> mode = new ModeValue<>("Mode", "Packet", "Packet", "Hypixel", "Packet loss", "Legit");

    private final NumberValue<Integer> horizontal = new NumberValue<>("Horizontal", () -> mode.is("Packet"), 0, 0, 100, 2);
    private final NumberValue<Integer> vertical = new NumberValue<>("Vertical", () -> mode.is("Packet"), 0, 0, 100, 2);

    private boolean reducing;

    private boolean pendingVelocity;

    private double motionY;

    private int ticks;

    private int offGroundTicks;

    private boolean wasVelocityEffective;

    private Blink blinkModule;
    private Backtrack backtrackModule;
    private Longjump longjumpModule;
    private Speed speedModule;

    public Velocity() {
        super("Velocity", ModuleCategory.COMBAT);
        this.addValues(mode, horizontal, vertical);
    }

    @Override
    public void onEnable() {
        reducing = false;
        offGroundTicks = 0;

        wasVelocityEffective = false;
    }

    @Override
    public void onDisable() {
        if(mode.is("Hypixel") && pendingVelocity) {
            pendingVelocity = false;
            mc.thePlayer.motionY = motionY;
            YolBi.instance.getPacketBlinkHandler().stopBlinkingPing();
        }

        if(mode.is("Packet loss")) {
            YolBi.instance.getPacketBlinkHandler().stopAll();
        }
    }

    @Override
    public void onClientStarted() {
        blinkModule = YolBi.instance.getModuleManager().getModule(Blink.class);
        backtrackModule = YolBi.instance.getModuleManager().getModule(Backtrack.class);
        longjumpModule = YolBi.instance.getModuleManager().getModule(Longjump.class);
        speedModule = YolBi.instance.getModuleManager().getModule(Speed.class);
    }

    @Listener
    public void onReceive(PacketReceiveEvent event) {
        if(canEditVelocity()) {
            if(event.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = event.getPacket();

                if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                    switch (mode.getValue()) {
                        case "Packet":
                            double horizontalMult = horizontal.getValue() / 100.0;
                            double verticalMult = vertical.getValue() / 100.0;

                            if(horizontalMult == 0) {
                                event.setCancelled(true);

                                if(verticalMult > 0) {
                                    mc.thePlayer.motionY = (packet.getMotionY() * verticalMult) / 8000.0;
                                }
                            } else {
                                packet.setMotionX((int) (packet.getMotionX() * horizontalMult));
                                packet.setMotionZ((int) (packet.getMotionZ() * horizontalMult));

                                packet.setMotionY((int) (packet.getMotionY() * verticalMult));
                            }
                            break;
                        case "Hypixel":
                            event.setCancelled(true);

                            if(offGroundTicks == 1 || !speedModule.isEnabled()) {
                                mc.thePlayer.motionY = packet.getMotionY() / 8000.0;
                            } else {
                                pendingVelocity = true;

                                motionY = packet.getMotionY() / 8000.0;

                                YolBi.instance.getPacketBlinkHandler().startBlinkingPing();

                                ticks = 12;
                            }
                            break;
                        case "Packet loss":
                            event.setCancelled(true);
                            pendingVelocity = true;
                            break;
                        case "Legit":
                            if(mc.currentScreen == null) {
                                mc.gameSettings.keyBindSprint.setPressed(true);
                                mc.gameSettings.keyBindForward.setPressed(true);
                                mc.gameSettings.keyBindJump.setPressed(true);
                                mc.gameSettings.keyBindBack.setPressed(false);

                                reducing = true;
                            }
                            break;
                    }
                }
            }
        } else {
            switch (mode.getValue()) {
                case "Hypixel":
                    if(pendingVelocity) {
                        pendingVelocity = false;
                        mc.thePlayer.motionY = motionY;
                        YolBi.instance.getPacketBlinkHandler().stopBlinkingPing();
                    }
                    break;
            }
        }
    }

    private boolean canEditVelocity() {
        boolean usingSelfDamageLongjump = longjumpModule.isEnabled() && longjumpModule.mode.is("Self damage");

        return !blinkModule.isEnabled() && !backtrackModule.isDelaying() && !usingSelfDamageLongjump;
    }

    @Listener
    public void onUpdate(UpdateEvent event) {
        if(mc.thePlayer.onGround) {
            offGroundTicks = 0;
        } else {
            offGroundTicks++;
        }

        switch (mode.getValue()) {
            case "Hypixel":
                --ticks;

                if(pendingVelocity) {
                    if(offGroundTicks == 1 || !YolBi.instance.getPacketBlinkHandler().isBlinkingPing() || ticks == 1) {
                        pendingVelocity = false;
                        mc.thePlayer.motionY = motionY;
                        YolBi.instance.getPacketBlinkHandler().stopBlinkingPing();
                    }

                    mc.gameSettings.keyBindJump.setPressed(false);
                }
                break;
            case "Packet loss":
                YolBi.instance.getPacketBlinkHandler().startBlinkingPing();

                if(pendingVelocity) {
                    YolBi.instance.getPacketBlinkHandler().clearPing();
                    pendingVelocity = false;
                } else {
                    YolBi.instance.getPacketBlinkHandler().releasePing();
                }
                break;
        }
    }

    @Listener
    public void onPostMotion(PostMotionEvent event) {
        switch (mode.getValue()) {
            case "Legit":
                if(reducing) {
                    if(mc.currentScreen == null) {
                        KeyboardUtil.resetKeybindings(mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindForward,
                                mc.gameSettings.keyBindJump, mc.gameSettings.keyBindBack);
                    }

                    reducing = false;
                }
                break;
        }
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

}