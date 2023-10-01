package cn.yapeteam.yolbi.event.impl.player;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventMotion extends Event {

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

}