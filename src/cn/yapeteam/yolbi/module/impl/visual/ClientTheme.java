package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.impl.RenderEvent;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.EventListenType;
import cn.yapeteam.yolbi.module.Module;

import java.awt.*;

public class ClientTheme extends Module {

    public final ModeValue color = new ModeValue("Color", "Blue", "White", "Blue", "Custom static", "Custom fade", "Custom 3 colors", "Rainbow");

    private final NumberValue red = new NumberValue("Red", () -> color.getValue().startsWith("Custom"), 210, 0, 255, 5);
    private final NumberValue green = new NumberValue("Green", () -> color.getValue().startsWith("Custom"), 80, 0, 255, 5);
    private final NumberValue blue = new NumberValue("Blue", () -> color.getValue().startsWith("Custom"), 105, 0, 255, 5);

    private final NumberValue red2 = new NumberValue("Red 2", () -> color.is("Custom fade") || color.is("Custom 3 colors"), 135, 0, 255, 5);
    private final NumberValue green2 = new NumberValue("Green 2", () -> color.is("Custom fade") || color.is("Custom 3 colors"), 190, 0, 255, 5);
    private final NumberValue blue2 = new NumberValue("Blue 2", () -> color.is("Custom fade") || color.is("Custom 3 colors"), 255, 0, 255, 5);

    private final NumberValue red3 = new NumberValue("Red 3", () -> color.is("Custom 3 colors"), 0, 0, 255, 5);
    private final NumberValue green3 = new NumberValue("Green 3", () -> color.is("Custom 3 colors"), 255, 0, 255, 5);
    private final NumberValue blue3 = new NumberValue("Blue 3", () -> color.is("Custom 3 colors"), 255, 0, 255, 5);

    private final NumberValue saturation = new NumberValue("Saturation", () -> color.is("Rainbow"), 0.9, 0.05, 1, 0.05);
    private final NumberValue brightness = new NumberValue("Brightness", () -> color.is("Rainbow"), 0.9, 0.05, 1, 0.05);

    private Color color1, color2, color3;

    private boolean colorsSet;

    public ClientTheme() {
        super("Client theme", ModuleCategory.VISUAL);
        this.addValues(color, red, green, blue, red2, green2, blue2, red3, green3, blue3, saturation, brightness);

        this.listenType = EventListenType.MANUAL;
        this.startListening();
    }

    @Override
    public void onEnable() {
        this.setEnabled(false);
    }

    @Listener(Priority.HIGHER)
    public void onRender(RenderEvent event) {
        setColors();
        colorsSet = true;
    }

    public int getColor(int offset) {
        if(!colorsSet) {
            setColors();
            colorsSet = true;
        }

        switch (color.getValue()) {
            case "White":
                return -1;
            case "Blue":
                return ColorUtil.getColor(new Color(5, 138, 255), new Color(0, 35, 206), 2500, offset);
            case "Custom static":
                return color1.getRGB();
            case "Custom fade":
                return ColorUtil.getColor(color1, color2, 2500, offset);
            case "Custom 3 colors":
                return ColorUtil.getColor(color1, color2, color3, 3000, offset);
            case "Rainbow":
                return ColorUtil.getRainbow(4500, (int) (offset * 0.65), (float) saturation.getValue(), (float) brightness.getValue());
        }

        return -1;
    }

    private void setColors() {
        color1 = new Color(red.getValue(), green.getValue(), blue.getValue());
        color2 = new Color(red2.getValue(), green2.getValue(), blue2.getValue());
        color3 = new Color(red3.getValue(), green3.getValue(), blue3.getValue());
    }

}
