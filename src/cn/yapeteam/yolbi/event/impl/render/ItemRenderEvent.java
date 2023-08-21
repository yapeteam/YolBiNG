package cn.yapeteam.yolbi.event.impl.render;

import lombok.AllArgsConstructor;
import lombok.Setter;
import cn.yapeteam.yolbi.event.Event;

@Setter
@AllArgsConstructor
public class ItemRenderEvent extends Event {

    private boolean renderBlocking;

    public boolean shouldRenderBlocking() {
        return renderBlocking;
    }

}