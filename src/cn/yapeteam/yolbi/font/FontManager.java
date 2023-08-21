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
            PingFangBold18;


    public FontManager() {
        productSans = new VestigeFontRenderer(getFontFromTTF("product_sans", 20, Font.PLAIN), true, true);
        productSans23 = new VestigeFontRenderer(getFontFromTTF("product_sans", 23, Font.PLAIN), true, true);
        productSansTitle = new VestigeFontRenderer(getFontFromTTF("product_sans", 34, Font.PLAIN), true, true);
        comfortaa = new VestigeFontRenderer(getFontFromTTF("comfortaa", 19, Font.PLAIN), true, true);

        /*PingFang12 = getUnicode("PingFang_Normal", 12);
        PingFang14 = getUnicode("PingFang_Normal", 14);
        PingFang16 = getUnicode("PingFang_Normal", 16);
        PingFang18 = getUnicode("PingFang_Normal", 18);
        PingFangBold18 = getUnicode("PingFang_Bold", 18);*/
        PingFang12 = new FontRenderer(getFontFromTTF("PingFang_Normal", 12, Font.PLAIN), 12, true);
        PingFang14 = new FontRenderer(getFontFromTTF("PingFang_Normal", 14, Font.PLAIN), 14, true);
        PingFang16 = new FontRenderer(getFontFromTTF("PingFang_Normal", 16, Font.PLAIN), 14, true);
        PingFang18 = new FontRenderer(getFontFromTTF("PingFang_Normal", 18, Font.PLAIN), 14, true);
        PingFangBold18 = new FontRenderer(getFontFromTTF("PingFang_Bold", 18, Font.PLAIN), 14, true);
        FLUXICON14 = new VestigeFontRenderer(getFontFromTTF("fluxicon", 14, Font.PLAIN), true, true);
        /*PingFang12 = new WrappedVertexFontRenderer(new VertexFontRenderer(getFontFromTTF("PingFang_Normal", 12, Font.PLAIN)), 12);
        PingFang14 = new WrappedVertexFontRenderer(new VertexFontRenderer(getFontFromTTF("PingFang_Normal", 14, Font.PLAIN)), 14);
        PingFang16 = new WrappedVertexFontRenderer(new VertexFontRenderer(getFontFromTTF("PingFang_Normal", 16, Font.PLAIN)), 16);
        PingFang18 = new WrappedVertexFontRenderer(new VertexFontRenderer(getFontFromTTF("PingFang_Normal", 18, Font.PLAIN)), 18);
        PingFangBold18 = new WrappedVertexFontRenderer(new VertexFontRenderer(getFontFromTTF("PingFang_Bold", 18, Font.PLAIN)), 18);*/
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
