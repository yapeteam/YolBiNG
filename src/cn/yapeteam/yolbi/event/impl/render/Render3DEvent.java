package cn.yapeteam.yolbi.event.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@AllArgsConstructor
public class Render3DEvent extends Event {

    private float partialTicks;

}
