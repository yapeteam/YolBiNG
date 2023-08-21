package cn.yapeteam.yolbi.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@AllArgsConstructor
public class Render3DEvent extends Event {

    private float partialTicks;

}
