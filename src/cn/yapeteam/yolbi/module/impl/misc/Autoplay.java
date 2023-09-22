package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.TickEvent;
import cn.yapeteam.yolbi.event.impl.network.PacketReceiveEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.misc.TimerUtil;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(name = "Autoplay", category = ModuleCategory.MISC)
public class Autoplay extends Module {

    private final ModeValue<String> mode = new ModeValue<>("Mode", "Solo insane", "Solo normal", "Solo insane");

    private final NumberValue<Integer> delay = new NumberValue<>("Delay", 1500, 0, 4000, 50);

    private final String winMessage = "You won! Want to play again? Click here!",
            loseMessage = "You died! Want to play again? Click here!";

    private final TimerUtil timer = new TimerUtil();

    private boolean waiting;

    public Autoplay() {
        this.addValues(mode, delay);
    }

    @Override
    public void onEnable() {
        waiting = false;
        timer.reset();
    }

    @Listener
    public void onTick(TickEvent event) {
        if (waiting && timer.getTimeElapsed() >= delay.getValue()) {
            String command = "";

            switch (mode.getValue()) {
                case "Solo normal":
                    command = "/play solo_normal";
                    break;
                case "Solo insane":
                    command = "/play solo_insane";
                    break;
            }

            mc.thePlayer.sendChatMessage(command);

            timer.reset();
            waiting = false;
        }
    }

    @Listener
    public void onReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = event.getPacket();

            String message = packet.getChatComponent().getUnformattedText();

            if ((message.contains(winMessage) && message.length() < winMessage.length() + 3) || (message.contains(loseMessage) && message.length() < loseMessage.length() + 3)) {
                waiting = true;
                timer.reset();
            }
        }
    }

}
