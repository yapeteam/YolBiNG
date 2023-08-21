package cn.yapeteam.yolbi.event.impl.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@AllArgsConstructor
public class KeyPressEvent extends Event {

    private int key;

}