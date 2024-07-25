package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.EventListenType;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import cn.yapeteam.yolbi.values.impl.ColorValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@ModuleInfo(name = "ClientTheme", category = ModuleCategory.VISUAL)
public class ClientTheme extends Module {
    public final ModeValue<String> color = new ModeValue<>("Color", "Custom fade", "White", "Blue", "Vape", "Custom static", "Custom fade", "Custom 3 colors", "Rainbow");
    private final ColorValue color1 = new ColorValue("Color1", () -> color.getValue().startsWith("Custom"), new Color(210, 80, 105).getRGB());
    private final ColorValue color2 = new ColorValue("Color2", () -> color.getValue().startsWith("Custom"), new Color(135, 190, 255).getRGB());
    private final ColorValue color3 = new ColorValue("Color3", () -> color.getValue().startsWith("Custom"), new Color(0, 255, 255).getRGB());
    private final NumberValue<Double> saturation = new NumberValue<>("Saturation", () -> color.is("Rainbow"), 0.9, 0.05, 1.0, 0.05);
    private final NumberValue<Double> brightness = new NumberValue<>("Brightness", () -> color.is("Rainbow"), 0.9, 0.05, 1.0, 0.05);

    public ClientTheme() {
        String[] languages = new String[YolBi.instance.getLanguageManager().getLanguages().size()];
        for (int i = 0; i < languages.length; i++)
            languages[i] = YolBi.instance.getLanguageManager().getLanguages().get(i).getName();
        ModeValue<String> language = getLanguage(languages);
        this.addValues(language, color, color1, color2, color3, saturation, brightness);
        this.listenType = EventListenType.MANUAL;
        this.startListening();
    }

    @NotNull
    private static ModeValue<String> getLanguage(String[] languages) {
        ModeValue<String> language = new ModeValue<>("Language", "English", languages);
        language.setCallback((oldV, newV) -> {
            if (YolBi.instance.getLanguageManager().getLanguages().stream().noneMatch(l -> l.getName().equals(newV))) {
                System.err.println("Language noneMatch: " + newV);
                YolBi.instance.getLanguageManager().getLanguages().forEach(System.out::println);
                return oldV;
            }
            YolBi.instance.getLanguageManager().setCurrent(newV);
            return newV;
        });
        return language;
    }

    @Override
    public void onEnable() {
        this.setEnabled(false);
    }

    public int getColor(int offset) {
        switch (color.getValue()) {
            case "White":
                return -1;
            case "Blue":
                return ColorUtil.getColor(new Color(5, 138, 255), new Color(0, 35, 206), 2500, offset);
            case "Vape":
                return new Color(5, 134, 105).getRGB();
            case "Custom static":
                return color1.getValue().getRGB();
            case "Custom fade":
                return ColorUtil.getColor(color1.getValue(), color2.getValue(), 2500, offset);
            case "Custom 3 colors":
                return ColorUtil.getColor(color1.getValue(), color2.getValue(), color3.getValue(), 3000, offset);
            case "Rainbow":
                return ColorUtil.getRainbow(4500, (int) (offset * 0.65), saturation.getValue().floatValue(), brightness.getValue().floatValue());
        }

        return -1;
    }
}
