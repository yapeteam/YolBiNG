package cn.yapeteam.yolbi.event.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@Setter
@AllArgsConstructor
public class EntityActionEvent extends Event {

    private boolean sprinting, sneaking;

}
