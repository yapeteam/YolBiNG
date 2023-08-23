package cn.yapeteam.yolbi.event.impl.render;


import lombok.AllArgsConstructor;
import lombok.Getter;
import cn.yapeteam.yolbi.event.Event;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@AllArgsConstructor
public class RenderEvent extends Event {

    private float partialTicks;
    private ScaledResolution scaledresolution;

}
