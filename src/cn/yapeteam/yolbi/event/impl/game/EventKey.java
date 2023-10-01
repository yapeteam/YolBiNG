package cn.yapeteam.yolbi.event.impl.game;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventKey extends Event {
    private int key;
}