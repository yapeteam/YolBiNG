package cn.yapeteam.yolbi.event.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@Setter
@AllArgsConstructor
public class MotionEvent extends Event {

    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

}