package cn.yapeteam.yolbi.event.type;

import lombok.Getter;
import lombok.Setter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@Setter
public class CancellableEvent extends Event {

    private boolean cancelled;

}