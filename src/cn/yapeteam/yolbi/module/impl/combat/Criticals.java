package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.MotionEvent;
import cn.yapeteam.yolbi.event.impl.player.MoveEvent;
import cn.yapeteam.yolbi.event.impl.network.PacketSendEvent;
import cn.yapeteam.yolbi.event.impl.player.PostMotionEvent;
import cn.yapeteam.yolbi.module.impl.movement.Speed;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import cn.yapeteam.yolbi.util.player.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Criticals extends Module {

    private final ModeValue<String> mode = new ModeValue<>("Mode", "Packet", "Packet", "Minijump", "NCP","Edit");

    private final NumberValue<Integer> hurtTime = new NumberValue<>("Hurt time", () -> mode.is("Minijump"), 2, 1, 6, 1);
    private final NumberValue<Double> motionY = new NumberValue<>("Motion Y", () -> mode.is("Minijump"), 0.08, 0.005, 0.42, 0.005);
    private final BooleanValue normalGravity = new BooleanValue("Normal gravity", () -> mode.is("Minijump"), true);
    private final NumberValue<Double> xzMotionMult = new NumberValue<>("XZ motion mult", () -> mode.is("Minijump"), 1.0, 0.0, 1.0, 0.02);

    private Killaura killauraModule;
    private Speed speedModule;

    private boolean stage;
    private int groundTicks;

    public Criticals() {
        super("Criticals", ModuleCategory.COMBAT);
        this.addValues(mode, hurtTime, normalGravity, xzMotionMult);
    }

    @Override
    public void onClientStarted() {
        speedModule = YolBi.instance.getModuleManager().getModule(Speed.class);
        killauraModule = YolBi.instance.getModuleManager().getModule(Killaura.class);
    }

    @Listener
    public void onSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();

            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                Entity target = packet.getEntity();

                switch (mode.getValue()) {
                    case "Packet":
                        if (mc.thePlayer.onGround) {
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1E-5, mc.thePlayer.posZ, false));
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                        } else {
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1E-5, mc.thePlayer.posZ, false));
                        }
                        break;
                    case "NCP":
                        if (target instanceof EntityPlayer) {
                            EntityPlayer player = (EntityPlayer) target;

                            if (player.hurtTime <= 4 && mc.thePlayer.onGround && !YolBi.instance.getModuleManager().getModule(Speed.class).isEnabled()) {
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ, false));
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                            }
                        }
                        break;
                }
            }
        }
    }

    @Listener
    public void onMove(MoveEvent event) {
        if (killauraModule.isEnabled() && killauraModule.getTarget() != null) {
            if (killauraModule.getTarget() instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) killauraModule.getTarget();

                if (mode.getValue().equals("Minijump")) {
                    if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && !speedModule.isEnabled()) {
                        if (target.hurtTime == hurtTime.getValue()) {
                            if (normalGravity.getValue()) {
                                event.setY(mc.thePlayer.motionY = motionY.getValue());
                            } else {
                                event.setY(motionY.getValue());
                            }

                            MovementUtil.motionMult(event, xzMotionMult.getValue());
                        }
                    }
                }
            }
        }
    }

    @Listener
    public void onMotion(MotionEvent event){
        if (mode.is("Edit")){
            if (killauraModule.isEnabled() && killauraModule.getTarget() != null) {
                if (killauraModule.getTarget() instanceof EntityPlayer) {
                    if (event.isOnGround()){
                        groundTicks++;
                        if(groundTicks > 2) {
                            stage = !stage;
                            event.setY(event.getY() + (stage ? 0.015 : 0.01) - Math.random() * 0.0001);
                            event.setOnGround(false);
                        }
                    }else {
                        groundTicks = 0;
                    }
                }
            }
        }

    }



}
