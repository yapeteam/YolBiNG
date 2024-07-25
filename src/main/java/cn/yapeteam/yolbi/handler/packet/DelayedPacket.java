package cn.yapeteam.yolbi.handler.packet;

import lombok.Getter;
import net.minecraft.network.Packet;
import cn.yapeteam.yolbi.util.misc.TimerUtil;

public class DelayedPacket {

    private final Packet packet;

    @Getter
    private final TimerUtil timer;

    public DelayedPacket(Packet packet) {
        this.packet = packet;
        this.timer = new TimerUtil();
    }

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

}
