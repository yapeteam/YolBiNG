package cn.yapeteam.yolbi.event.impl.render;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class EventItemRender extends Event {

    private boolean renderBlocking;

    public boolean shouldRenderBlocking() {
        return renderBlocking;
    }

}