package cn.yapeteam.yolbi.font;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

@Getter
public class FontManager {
    private final AbstractFontRenderer
            productSans,
            productSans23,
            productSansTitle,
            comfortaa,
            FLUXICON14,
            PingFang12,
            PingFang14,
            PingFang16,
            PingFang18,
            PingFang19,
            PingFang20,
            PingFang25,
            PingFang36,
            PingFang72,
            PingFangBold18,
            LightBeach72,
            MainIcon36;


    public FontManager() {
        productSans = new FontRenderer(getFontFromTTF("product_sans", 20, Font.PLAIN), true);
        productSans23 = new FontRenderer(getFontFromTTF("product_sans", 23, Font.PLAIN), true);
        productSansTitle = new FontRenderer(getFontFromTTF("product_sans", 34, Font.PLAIN), true);
        comfortaa = new FontRenderer(getFontFromTTF("comfortaa", 19, Font.PLAIN), true);
        PingFang12 = new FontRenderer(getFontFromTTF("PingFang_Normal", 12, Font.PLAIN), true);
        PingFang14 = new FontRenderer(getFontFromTTF("PingFang_Normal", 14, Font.PLAIN), true);
        PingFang16 = new FontRenderer(getFontFromTTF("PingFang_Normal", 16, Font.PLAIN), true);
        PingFang18 = new FontRenderer(getFontFromTTF("PingFang_Normal", 18, Font.PLAIN), true);
        PingFang19 = new FontRenderer(getFontFromTTF("PingFang_Normal", 19, Font.PLAIN), true);
        PingFang20 = new FontRenderer(getFontFromTTF("PingFang_Normal", 20, Font.PLAIN), true);
        PingFang25 = new FontRenderer(getFontFromTTF("PingFang_Normal", 25, Font.PLAIN), true);
        PingFang36 = new FontRenderer(getFontFromTTF("PingFang_Normal", 36, Font.PLAIN), true);
        PingFang72 = new FontRenderer(getFontFromTTF("PingFang_Normal", 72, Font.PLAIN), true);
        PingFangBold18 = new FontRenderer(getFontFromTTF("PingFang_Bold", 18, Font.PLAIN), true);
        FLUXICON14 = new FontRenderer(getFontFromTTF("fluxicon", 14, Font.PLAIN), true);
        LightBeach72 = new FontRenderer(getFontFromTTF("LightBeach", 72, Font.PLAIN), true);
        MainIcon36 = new FontRenderer(getFontFromTTF("mainicons", 36, Font.PLAIN), true);
    }

    public Font getFontFromTTF(String fontName, float fontSize, int fontType) {
        Font output = null;

        ResourceLocation fontLocation = new ResourceLocation("yolbi/fonts/" + fontName + ".ttf");

        try {
            output = Font.createFont(fontType, Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
            output = output.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
