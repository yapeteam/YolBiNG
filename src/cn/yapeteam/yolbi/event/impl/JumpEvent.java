package cn.yapeteam.yolbi.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@Setter
@AllArgsConstructor
public class JumpEvent extends Event {

    private double motionY;
    private float yaw;
    private boolean boosting;

}