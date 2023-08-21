package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.event.impl.MotionEvent;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Nofall extends Module {

    private final ModeValue mode = new ModeValue("Mode", "Spoof", "Packet", "Spoof", "Blink");

    private final NumberValue blinkTicks = new NumberValue("Blink ticks", () -> mode.is("Blink"), 6, 2, 14, 1);
    private final BooleanValue suspendAll = new BooleanValue("Suspend all packets", () -> mode.is("Blink"), true);

    private double lastY;
    private double fallDistance;

    private int ticks;

    private boolean blinking;

    public Nofall() {
        super("Nofall", ModuleCategory.PLAYER);
        this.addValues(mode, blinkTicks, suspendAll);
    }

    @Override
    public void onEnable() {
        ticks = 0;

        lastY = mc.thePlayer.posY;
        fallDistance = mc.thePlayer.fallDistance;
    }

    @Override
    public void onDisable() {
        Vestige.instance.getPacketBlinkHandler().stopAll();
    }

    @Listener
    public void onMotion(MotionEvent event) {
        double y = event.getY();

        double motionY = y - lastY;

        if(mc.thePlayer.onGround) {
            fallDistance = 0;
        } else {
            if(motionY < 0) {
                fallDistance -= motionY;
            }
        }

        switch (mode.getValue()) {
            case "Packet":
                if(fallDistance >= 3) {
                    PacketUtil.sendPacket(new C03PacketPlayer(true));
                    fallDistance = 0;
                }
                break;
            case "Spoof":
                if(fallDistance >= 3) {
                    event.setOnGround(true);
                    fallDistance = 0;
                }
                break;
            case "Blink":
                if(fallDistance >= 3) {
                    if(!blinking) {
                        if(suspendAll.getValue()) {
                            Vestige.instance.getPacketBlinkHandler().startBlinkingAll();
                        } else {
                            Vestige.instance.getPacketBlinkHandler().startBlinkingMove();
                            Vestige.instance.getPacketBlinkHandler().startBlinkingOther();
                        }

                        ticks = 0;

                        blinking = true;
                    }
                    event.setOnGround(true);
                    fallDistance = 0;
                }

                if(blinking) {
                    if(ticks >= blinkTicks.getValue()) {
                        ticks = 0;
                        blinking = false;

                        Vestige.instance.getPacketBlinkHandler().stopAll();
                    } else {
                        ticks++;
                    }
                }
                break;
        }

        lastY = event.getY();
    }

}
