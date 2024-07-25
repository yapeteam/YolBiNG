package cn.yapeteam.yolbi.event.impl.render;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class EventRotationsRender extends Event {

	@Setter
	private float yaw, bodyYaw, pitch;

	private float partialTicks;
	
}
