package cn.yapeteam.yolbi.font;

import cn.yapeteam.yolbi.font.cfont.VestigeFontRenderer;
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
            PingFang36,
            PingFang72,

    PingFangBold18;


    public FontManager() {
        productSans = new VestigeFontRenderer(getFontFromTTF("product_sans", 20, Font.PLAIN), true, true);
        productSans23 = new VestigeFontRenderer(getFontFromTTF("product_sans", 23, Font.PLAIN), true, true);
        productSansTitle = new VestigeFontRenderer(getFontFromTTF("product_sans", 34, Font.PLAIN), true, true);
        comfortaa = new VestigeFontRenderer(getFontFromTTF("comfortaa", 19, Font.PLAIN), true, true);
        PingFang12 = new FontRenderer(getFontFromTTF("PingFang_Normal", 12, Font.PLAIN), 12, true);
        PingFang14 = new FontRenderer(getFontFromTTF("PingFang_Normal", 14, Font.PLAIN), 14, true);
        PingFang16 = new FontRenderer(getFontFromTTF("PingFang_Normal", 16, Font.PLAIN), 16, true);
        PingFang18 = new FontRenderer(getFontFromTTF("PingFang_Normal", 18, Font.PLAIN), 18, true);
        PingFang19 = new FontRenderer(getFontFromTTF("PingFang_Normal", 19, Font.PLAIN), 19, true);
        PingFang20 = new FontRenderer(getFontFromTTF("PingFang_Normal", 20, Font.PLAIN), 20, true);
        PingFang36 = new FontRenderer(getFontFromTTF("PingFang_Normal", 36, Font.PLAIN), 36, true);
        PingFang72 = new FontRenderer(getFontFromTTF("PingFang_Normal", 72, Font.PLAIN), 72, true);

        PingFangBold18 = new FontRenderer(getFontFromTTF("PingFang_Bold", 18, Font.PLAIN), 18, true);
        FLUXICON14 = new VestigeFontRenderer(getFontFromTTF("fluxicon", 14, Font.PLAIN), true, true);
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
