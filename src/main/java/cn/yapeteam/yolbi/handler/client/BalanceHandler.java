package cn.yapeteam.yolbi.handler.client;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventFinalPacketSend;
import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
import cn.yapeteam.yolbi.util.IMinecraft;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@Getter
public class BalanceHandler implements IMinecraft {

    @Setter
    private long balance;

    private long lastNanoTime;

    public BalanceHandler() {
        YolBi.instance.getEventManager().register(this);
    }

    @Listener
    public void onReceive(EventPacketReceive event) {
        if(event.getPacket() instanceof S08PacketPlayerPosLook) {
            balance -= 50000000;
        }
    }

    @Listener
    public void onFinalSend(EventFinalPacketSend event) {
        if(event.getPacket() instanceof C03PacketPlayer) {
            if(!event.isCancelled()) {
                long nanoTime = System.nanoTime();

                if(!mc.getNetHandler().doneLoadingTerrain || mc.thePlayer.ticksExisted < 30) {
                    balance = 0;
                    lastNanoTime = nanoTime - 50000000;
                }

                balance += 50000000;

                balance -= nanoTime - lastNanoTime;

                lastNanoTime = nanoTime;
            }
        }
    }

    public long getBalanceInMS() {
        return (long) (balance / 1E6);
    }

}
