package cn.yapeteam.yolbi.event.impl.render;


import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@AllArgsConstructor
public class EventRender2D extends Event {

    private float partialTicks;
    private ScaledResolution scaledresolution;

}
