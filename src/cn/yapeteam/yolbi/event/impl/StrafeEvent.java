package cn.yapeteam.yolbi.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@Setter
@AllArgsConstructor
public class StrafeEvent extends Event {
	
	private float forward, strafe;
	private float friction, attributeSpeed;
	private float yaw;
	
}