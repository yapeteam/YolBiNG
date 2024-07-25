package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventPostMotion;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.module.impl.movement.Blink;
import cn.yapeteam.yolbi.module.impl.movement.LongJump;
import cn.yapeteam.yolbi.module.impl.movement.Speed;
import cn.yapeteam.yolbi.util.player.KeyboardUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(name = "Velocity", category = ModuleCategory.COMBAT)
public class Velocity extends Module {
    public final ModeValue<String> mode = new ModeValue<>("Mode", "Packet",
            "Packet", "Hypixel", "Packet loss", "Legit", "Intave", "Martix","RMC","BetterJump","Yawlegit","Sneak");
    private final NumberValue<Integer> horizontal = new NumberValue<>("Horizontal", () -> (mode.is("Packet") || mode.is("Intave")), 0, 0, 100, 2);
    private final NumberValue<Integer> vertical = new NumberValue<>("Vertical", () -> mode.is("Packet"), 0, 0, 100, 2);

    private final BooleanValue blinkHurt = new BooleanValue("BlinkHurt",false);//todo have bug
    private final NumberValue<Float> YawlegitModeset = new NumberValue<>("YawlegitModeset", ()->mode.is("Yawlegit"),1f,0f,2f,0.1f);

    private final NumberValue<Float> YawlegitAngleOffset = new NumberValue<>("AngleOffset", ()->mode.is("Yawlegit"),0f,-180f,180f,0.1f);



    private boolean reducing;
    private boolean flag;
    private boolean pendingVelocity;
    private double motionY;
    private int ticks;
    private int offGroundTicks;
    private Blink blinkModule;
    private Backtrack backtrackModule;
    private LongJump longjumpModule;
    private Speed speedModule;
    private int PacketDelay;
    private int Bticks;
    private int Bf;
    private int Ba;
    private int BResetDelay;
    private int BNeedtoUse;
    private int BResetPacket;
    private int BStart;
    private int Bvelo;
    private int Bhurt;
    private boolean BblinkRunning;
    private int BStart2;

    public Velocity() {
        this.addValues(mode, horizontal, vertical,YawlegitModeset,YawlegitAngleOffset);
    }

    @Override
    public void onEnable() {
        reducing = false;
        offGroundTicks = 0;

    }

    @Override
    public void onDisable() {

        if (mode.is("Hypixel") && pendingVelocity) {
            pendingVelocity = false;
            mc.thePlayer.motionY = motionY;
            YolBi.instance.getPacketBlinkHandler().stopBlinkingPing();
        }

        if (mode.is("Packet loss")) {
            YolBi.instance.getPacketBlinkHandler().stopAll();
        }

        if ("RMC BetterJump Yawlegit Sneak".contains(mode.getValue())){
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
            } else {
                mc.gameSettings.keyBindForward.setPressed(false);
            }
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
            } else {
                mc.gameSettings.keyBindForward.setPressed(false);
            }

            blinkModule.setEnabled(false);Bticks = 0;Bf = 0;Ba = 0;
            BResetDelay = 3;PacketDelay = 0;Bhurt = 0;Bvelo = 0;
            BStart = 0;BResetPacket = 0;BNeedtoUse = 0;
        }

    }

    @Override
    public void onClientStarted() {
        blinkModule = YolBi.instance.getModuleManager().getModule(Blink.class);
        backtrackModule = YolBi.instance.getModuleManager().getModule(Backtrack.class);
        longjumpModule = YolBi.instance.getModuleManager().getModule(LongJump.class);
        speedModule = YolBi.instance.getModuleManager().getModule(Speed.class);
    }

    @Listener
    public void onReceive(EventPacketReceive event) {
        if ("RMC BetterJump Yawlegit Sneak".contains(mode.getValue())){
            Packet packet = event.getPacket();
            switch (mode.getValue()) {
                case "RMC"://??grim matrix vulcan
                    BNeedtoUse = 1;
                    if (mc.thePlayer.onGround && mc.thePlayer.hurtTime >= 8) {
                        if (packet instanceof C03PacketPlayer) {
                            event.setCancelled(true);
                        }
                        if (PacketDelay == 0) {
                            if (packet instanceof C0FPacketConfirmTransaction) {
                                event.setCancelled(true);
                            }
                        }
                    }
                    if (PacketDelay == 0 && mc.thePlayer.onGround) {
                        if (packet instanceof S12PacketEntityVelocity) {
                            event.setCancelled(true);
                        }
                    }
                    break;
                case "Yawlegit":
                    BNeedtoUse = 0;
                    if (packet instanceof S12PacketEntityVelocity && mc.theWorld.getEntityByID(((S12PacketEntityVelocity) packet).getEntityID()) == mc.thePlayer) {
                        float yaw = -(mc.thePlayer.rotationYaw + YawlegitAngleOffset.getValue() + 180);
                        double velocity = Math.sqrt(((S12PacketEntityVelocity) packet).getMotionX() * ((S12PacketEntityVelocity) packet).getMotionX() + ((S12PacketEntityVelocity) packet).getMotionZ() * ((S12PacketEntityVelocity) packet).getMotionZ()) * YawlegitModeset.getValue();
                        ((S12PacketEntityVelocity) packet).setMotionX((int) (velocity * Math.sin(yaw / 180 * Math.PI)));
                        ((S12PacketEntityVelocity) packet).setMotionZ((int) (velocity * Math.cos(yaw / 180 * Math.PI)));
                    }
                    break;
            }
        }
        if (canEditVelocity()) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = event.getPacket();

                if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                    switch (mode.getValue()) {
                        case "Packet":
                            double horizontalMult = horizontal.getValue() / 100.0;
                            double verticalMult = vertical.getValue() / 100.0;

                            if (horizontalMult == 0) {
                                event.setCancelled(true);

                                if (verticalMult > 0) {
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

                            if (offGroundTicks == 1 || !speedModule.isEnabled()) {
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
                            if (mc.currentScreen == null) {
                                mc.gameSettings.keyBindSprint.setPressed(true);
                                mc.gameSettings.keyBindForward.setPressed(true);
                                mc.gameSettings.keyBindJump.setPressed(true);
                                mc.gameSettings.keyBindBack.setPressed(false);

                                reducing = true;
                            }
                            break;
                        case "Intave": {
                            if (mc.currentScreen == null) {
                                packet.setMotionX((int) (packet.getMotionX() * horizontal.getValue() / 100.0));
                                packet.setMotionZ((int) (packet.getMotionZ() * horizontal.getValue() / 100.0));
                                if (mc.thePlayer.hurtTime == 9) {
                                    mc.gameSettings.keyBindSprint.setPressed(true);
                                    mc.gameSettings.keyBindForward.setPressed(true);
                                    mc.gameSettings.keyBindJump.setPressed(true);
                                    mc.gameSettings.keyBindBack.setPressed(false);
                                }
                            }
                            break;
                        }
                        case "Martix": {
                            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                                if (!flag) {
                                    event.setCancelled(true);
                                } else {
                                    flag = false;
                                    packet.setMotionX((((int) ((double) packet.getMotionX() * -0.1))));
                                    packet.setMotionZ((((int) ((double) packet.getMotionZ() * -0.1))));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (mode.getValue().equals("Hypixel")) {
                if (pendingVelocity) {
                    pendingVelocity = false;
                    mc.thePlayer.motionY = motionY;
                    YolBi.instance.getPacketBlinkHandler().stopBlinkingPing();
                }
            }
        }




    }

    private boolean canEditVelocity() {
        boolean usingSelfDamageLongjump = longjumpModule.isEnabled() && longjumpModule.mode.is("Self damage");

        return !blinkModule.isEnabled() && !backtrackModule.isDelaying() && !usingSelfDamageLongjump;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;
        } else {
            offGroundTicks++;
        }

        switch (mode.getValue()) {
            case "Hypixel":
                --ticks;

                if (pendingVelocity) {
                    if (offGroundTicks == 1 || !YolBi.instance.getPacketBlinkHandler().isBlinkingPing() || ticks == 1) {
                        pendingVelocity = false;
                        mc.thePlayer.motionY = motionY;
                        YolBi.instance.getPacketBlinkHandler().stopBlinkingPing();
                    }

                    mc.gameSettings.keyBindJump.setPressed(false);
                }
                break;
            case "Packet loss":
                YolBi.instance.getPacketBlinkHandler().startBlinkingPing();

                if (pendingVelocity) {
                    YolBi.instance.getPacketBlinkHandler().clearPing();
                    pendingVelocity = false;
                } else {
                    YolBi.instance.getPacketBlinkHandler().releasePing();
                }
                break;
            case "Martix": {
                if (mc.thePlayer.hurtTime > 0 && !mc.thePlayer.onGround) {
                    double var3 = mc.thePlayer.rotationYaw * 0.017453292F;
                    double var5 = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
                    mc.thePlayer.motionX = -Math.sin(var3) * var5;
                    mc.thePlayer.motionZ = Math.cos(var3) * var5;
                    mc.thePlayer.setSprinting(mc.thePlayer.ticksExisted % 2 != 0);
                }
                break;
            }
        }

        if ("RMC BetterJump Yawlegit Sneak".contains(mode.getValue())){
            if (BNeedtoUse == 1) {
                while (mc.thePlayer.hurtTime >= 8) {
                    mc.gameSettings.keyBindJump.setPressed(true);
                    break;
                }
                while (mc.thePlayer.hurtTime >= 7 && !mc.gameSettings.keyBindForward.isPressed()) {
                    mc.gameSettings.keyBindForward.setPressed(true);
                    BStart = 1;
                    break;
                }
                if (mc.thePlayer.hurtTime < 7 && mc.thePlayer.hurtTime > 0) {
                    mc.gameSettings.keyBindJump.setPressed(false);
                    if (BStart == 1) {
                        mc.gameSettings.keyBindForward.setPressed(false);
                        BStart = 0;
                    }
                }
            }
            if (mc.thePlayer.onGround) {} else {
                if (blinkHurt.getValue()) {
                    if(mc.thePlayer.hurtTime >= 5){ //blink
                        blinkModule.setEnabled(true);
                        BblinkRunning = true;
                        BResetPacket = 1;
                    } else {
                        blinkModule.setEnabled(false);
                        BblinkRunning = false;
                        BResetPacket = 0;
                    };
                }
            }

            switch (mode.getValue()) {
                case "BetterJump":
                    BNeedtoUse = 1;
                    break;
                case "Sneak":
                    BNeedtoUse = 0;
                    if (mc.thePlayer.onGround) {
                        while (mc.thePlayer.hurtTime >= 8) {
                            mc.gameSettings.keyBindSneak.setPressed(true);
                            break;
                        }
                    }
                    while (mc.thePlayer.hurtTime >= 7 && mc.gameSettings.keyBindForward.isPressed() == false) {
                        mc.gameSettings.keyBindForward.setPressed(true);
                        BStart2 = 1;
                        break;
                    }
                    if (mc.thePlayer.hurtTime < 7 && mc.thePlayer.hurtTime > 0) {
                        mc.gameSettings.keyBindSneak.setPressed(false);
                        if (BStart2 == 1) {
                            mc.gameSettings.keyBindForward.setPressed(false);
                            BStart2 = 0;
                        }
                    }
                    break;
            }








        }

    }

    @Listener
    public void onPostMotion(EventPostMotion event) {
        PacketDelay = 0;
        if (mode.getValue().equals("Legit")) {
            if (reducing) {
                if (mc.currentScreen == null) {
                    KeyboardUtil.resetKeybindings(mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindForward,
                            mc.gameSettings.keyBindJump, mc.gameSettings.keyBindBack);
                }

                reducing = false;
            }
        }
    }
    @Listener
    public void onPreMotion(EventMotion eventMotion){
        PacketDelay = 1;
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

}