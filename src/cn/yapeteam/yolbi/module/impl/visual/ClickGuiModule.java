package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.event.impl.RenderEvent;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import org.lwjgl.input.Keyboard;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.ui.click.dropdown.DropdownClickGUI;

import java.awt.*;

public class ClickGuiModule extends Module {

    private DropdownClickGUI dropdownClickGUI;

    private final ModeValue<String> color = new ModeValue<>("Color", "Client theme", "Client theme", "Custom static", "Custom fade", "Custom 3 colors", "Rainbow");

    private final NumberValue<Integer> red = new NumberValue<>("Red", () -> color.getValue().startsWith("Custom"), 0, 0, 255, 5);
    private final NumberValue<Integer> green = new NumberValue<>("Green", () -> color.getValue().startsWith("Custom"), 0, 0, 255, 5);
    private final NumberValue<Integer> blue = new NumberValue<>("Blue", () -> color.getValue().startsWith("Custom"), 255, 0, 255, 5);

    private final NumberValue<Integer> red2 = new NumberValue<>("Red 2", () -> color.is("Custom fade") || color.is("Custom 3 colors"), 0, 0, 255, 5);
    private final NumberValue<Integer> green2 = new NumberValue<>("Green 2", () -> color.is("Custom fade") || color.is("Custom 3 colors"), 255, 0, 255, 5);
    private final NumberValue<Integer> blue2 = new NumberValue<>("Blue 2", () -> color.is("Custom fade") || color.is("Custom 3 colors"), 255, 0, 255, 5);

    private final NumberValue<Integer> red3 = new NumberValue<>("Red 3", () -> color.is("Custom 3 colors"), 0, 0, 255, 5);
    private final NumberValue<Integer> green3 = new NumberValue<>("Green 3", () -> color.is("Custom 3 colors"), 255, 0, 255, 5);
    private final NumberValue<Integer> blue3 = new NumberValue<>("Blue 3", () -> color.is("Custom 3 colors"), 255, 0, 255, 5);

    private final NumberValue<Double> saturation = new NumberValue<>("Saturation", () -> color.is("Rainbow"), 0.9, 0.05, 1.0, 0.05);
    private final NumberValue<Double> brightness = new NumberValue<>("Brightness", () -> color.is("Rainbow"), 0.9, 0.05, 1.0, 0.05);

    public final BooleanValue boxOnHover = new BooleanValue("Box on hover", false);
    public final BooleanValue boxOnSettings = new BooleanValue("Box on settings", boxOnHover::getValue, false);

    private Color color1, color2, color3;

    private ClientTheme theme;

    public ClickGuiModule() {
        super("ClickGUI", ModuleCategory.VISUAL);
        this.setKey(Keyboard.KEY_RSHIFT);
        this.addValues(color, red, green, blue, red2, green2, blue2, red3, green3, blue3, saturation, brightness, boxOnHover, boxOnSettings);
    }

    @Override
    public void onEnable() {
        if (dropdownClickGUI == null) {
            dropdownClickGUI = new DropdownClickGUI(this);
        }

        mc.displayGuiScreen(dropdownClickGUI);

        setColors();
    }

    @Override
    public void onClientStarted() {
        theme = Vestige.instance.getModuleManager().getModule(ClientTheme.class);
    }

    @Listener
    public void onRender(RenderEvent event) {
        setColors();
    }

    public int getColor(int offset) {
        switch (color.getValue()) {
            case "Client theme":
                return theme.getColor(offset);
            case "Custom static":
                return color1.getRGB();
            case "Custom fade":
                return ColorUtil.getColor(color1, color2, 2500, offset);
            case "Custom 3 colors":
                return ColorUtil.getColor(color1, color2, color3, 3000, offset);
            case "Rainbow":
                return ColorUtil.getRainbow(4500, (int) (offset * 0.85), saturation.getValue().floatValue(), brightness.getValue().floatValue());
        }

        return -1;
    }

    private void setColors() {
        color1 = new Color(red.getValue(), green.getValue(), blue.getValue());
        color2 = new Color(red2.getValue(), green2.getValue(), blue2.getValue());
        color3 = new Color(red3.getValue(), green3.getValue(), blue3.getValue());
    }

}
