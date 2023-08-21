package cn.yapeteam.yolbi.event.impl;

import cn.yapeteam.yolbi.event.type.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Setter;
import net.minecraft.network.Packet;

@AllArgsConstructor
public class FinalPacketSendEvent extends CancellableEvent {

    @Setter
    private Packet packet;

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

}