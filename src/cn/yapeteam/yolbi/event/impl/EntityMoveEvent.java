package cn.yapeteam.yolbi.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import cn.yapeteam.yolbi.event.Event;

@Getter
@AllArgsConstructor
public class EntityMoveEvent extends Event {

    private Entity entity;

}