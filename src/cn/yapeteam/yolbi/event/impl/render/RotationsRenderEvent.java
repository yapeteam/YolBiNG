package cn.yapeteam.yolbi.event.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.yapeteam.yolbi.event.Event;

@Getter
@AllArgsConstructor
public class RotationsRenderEvent extends Event {

	@Setter
	private float yaw, bodyYaw, pitch;

	private float partialTicks;
	
}
