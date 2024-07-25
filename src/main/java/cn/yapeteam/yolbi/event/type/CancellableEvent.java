package cn.yapeteam.yolbi.event.type;

import cn.yapeteam.yolbi.event.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancellableEvent extends Event {

    private boolean cancelled;
}