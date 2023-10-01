package cn.yapeteam.yolbi.event.impl.player;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;

@Getter
@AllArgsConstructor
public class EventEntityMove extends Event {
    private Entity entity;
}