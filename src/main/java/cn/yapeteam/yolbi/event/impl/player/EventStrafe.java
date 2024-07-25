package cn.yapeteam.yolbi.event.impl.player;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventStrafe extends Event {
	
	private float forward, strafe;
	private float friction, attributeSpeed;
	private float yaw;
	
}