package cn.yapeteam.yolbi.event.impl;

import cn.yapeteam.yolbi.event.type.CancellableEvent;
import lombok.AllArgsConstructor;
import net.minecraft.network.Packet;

@AllArgsConstructor
public class PacketReceiveEvent extends CancellableEvent {

    private Packet packet;

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

}