package cn.yapeteam.yolbi.util.render;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.util.IMinecraft;
import cn.yapeteam.yolbi.values.Visibility;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import net.minecraft.client.gui.FontRenderer;

public class FontUtil implements IMinecraft {

    private static FontRenderer mcFont;
    private static AbstractFontRenderer productSans, comfortaa;

    public static ModeValue<String> getFontSetting() {
        return new ModeValue<>("Font", "Minecraft", "Minecraft", "Product sans", "Comfortaa");
    }

    public static ModeValue<String> getFontSetting(Visibility visibility) {
        return new ModeValue<>("Font", visibility, "Minecraft", "Minecraft", "Product sans", "Comfortaa");
    }

    public static void initFonts() {
        mcFont = mc.fontRendererObj;
        productSans = YolBi.instance.getFontManager().getProductSans();
        comfortaa = YolBi.instance.getFontManager().getComfortaa();
    }

    public static void drawString(String font, String text, float x, float y, int color) {
        switch (font) {
            case "Minecraft":
                mcFont.drawString(text, x, y, color);
                break;
            case "Product sans":
                productSans.drawString(text, x, y, color);
                break;
            case "Comfortaa":
                comfortaa.drawString(text, x, y, color);
                break;
        }
    }

    public static void drawStringWithShadow(String font, String text, float x, float y, int color) {
        switch (font) {
            case "Minecraft":
                mcFont.drawStringWithShadow(text, x, y, color);
                break;
            case "Product sans":
                productSans.drawStringWithShadow(text, x, y, color);
                break;
            case "Comfortaa":
                comfortaa.drawStringWithShadow(text, x, y, color);
                break;
        }
    }

    public static double getStringWidth(String font, String s) {
        switch (font) {
            case "Product sans":
                return productSans.getStringWidth(s);
            case "Comfortaa":
                return comfortaa.getStringWidth(s);
            case "Minecraft":
            default:
                return mc.fontRendererObj.getStringWidth(s);
        }
    }

    public static float getFontHeight(String font) {
        switch (font) {
            case "Product sans":
                return productSans.getHeight();
            case "Comfortaa":
                return comfortaa.getHeight();
            case "Minecraft":
            default:
                return mc.fontRendererObj.FONT_HEIGHT;
        }
    }

}