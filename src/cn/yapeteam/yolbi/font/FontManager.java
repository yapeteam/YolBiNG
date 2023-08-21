package cn.yapeteam.yolbi.font;

import cn.yapeteam.yolbi.font.cfont.VestigeFontRenderer;
import cn.yapeteam.yolbi.font.unicode.UnicodeFontRenderer;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

@Getter
public class FontManager {

    private final AbstractFontRenderer
            productSans,
            productSans23,
            productSansTitle,
            comfortaa,
            PingFang12,
            PingFang14,
            PingFang16,
            PingFang18,
            PingFangBold18;

    public FontManager() {
        productSans = new VestigeFontRenderer(getFontFromTTF("product_sans", 20, Font.PLAIN), true, true);
        productSans23 = new VestigeFontRenderer(getFontFromTTF("product_sans", 23, Font.PLAIN), true, true);
        productSansTitle = new VestigeFontRenderer(getFontFromTTF("product_sans", 34, Font.PLAIN), true, true);
        comfortaa = new VestigeFontRenderer(getFontFromTTF("comfortaa", 19, Font.PLAIN), true, true);
        PingFang12 = getUnicode("PingFang_Normal", 12);
        PingFang14 = getUnicode("PingFang_Normal", 14);
        PingFang16 = getUnicode("PingFang_Normal", 16);
        PingFang18 = getUnicode("PingFang_Normal", 18);
        PingFangBold18 = getUnicode("PingFang_Bold", 18);
    }

    public UnicodeFontRenderer getUnicode(String name, int size) {
        return new UnicodeFontRenderer(getFontFromTTF(name, size, Font.PLAIN), -1, -1, false);
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
