package cn.yapeteam.yolbi.font.fast;

import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SuppressWarnings("unused")
public class CFontRenderer extends CFont implements AbstractFontRenderer {
    private final int[] colorCode = new int[32];

    public CFontRenderer(Font font, int cs) {
        super(font, cs);

        for (int index = 0; index < 32; ++index) {
            int noClue = (index >> 3 & 1) * 85;
            int red = (index >> 2 & 1) * 170 + noClue;
            int green = (index >> 1 & 1) * 170 + noClue;
            int blue = (index & 1) * 170 + noClue;

            if (index == 6)
                red += 85;

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
        }
    }

    public CFontRenderer(Font font) {
        super(font, 256);

        for (int index = 0; index < 32; ++index) {
            int noClue = (index >> 3 & 1) * 85;
            int red = (index >> 2 & 1) * 170 + noClue;
            int green = (index >> 1 & 1) * 170 + noClue;
            int blue = (index & 1) * 170 + noClue;

            if (index == 6)
                red += 85;

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
        }
    }

    public float drawString(String text, double x, double y, int color) {
        return drawString(text, x, y, color, false);
    }

    public float drawStringWithShadow(String text, double x, double y, int color) {
        float shadowWidth = drawString(text, x + .7F, y + .7F, color, true);
        return Math.max(shadowWidth, drawString(text, x, y, color, false));
    }

    public float drawCenteredString(String text, double x, double y, int color) {
        return drawString(text, x - getStringWidth(text) / 2F, y, color);
    }

    public float drawCenteredStringWithShadow(String text, double x, double y, int color) {
        return drawStringWithShadow(text, x - getStringWidth(text) / 2F, y, color);
    }

    public void drawOutlinedString(String str, float x, float y, int internalCol, int externalCol) {
        this.drawString(str, x - 0.5f, y, externalCol);
        this.drawString(str, x + 0.5f, y, externalCol);
        this.drawString(str, x, y - 0.5f, externalCol);
        this.drawString(str, x, y + 0.5f, externalCol);
        this.drawString(str, x, y, internalCol);
    }

    private float drawString(String text, double x, double y, int color, boolean shadow) {
        x -= 1;
        y -= 2.5f;
        GlStateManager.resetColor();
        if (text == null) return 0;
        text = processString(text);

        final int textLength = text.length();

        x *= 2;

        y = (y - 1) * 2;

        if ((color & -67108864) == 0) {
            color |= -16777216;
        }

        if (shadow)
            color = (color & 16579836) >> 2 | color & -16777216;

        final float red = (color >> 16 & 0xFF) / 255F;
        final float green = (color >> 8 & 0xFF) / 255F;
        final float blue = (color & 0xFF) / 255F;
        final float alpha = (color >> 24 & 0xFF) / 255F;

        GlStateManager.pushMatrix();

        GlStateManager.scale(.5, .5, .5);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(texture.getGlTextureId());

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());

        for (int i = 0; i < textLength; i++) {

            final char character = text.charAt(i);

            if (character == '\247') {
                int colorIndex = 21;

                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                } catch (Exception ignored) {
                }

                if (colorIndex < 16) {
                    GlStateManager.bindTexture(texture.getGlTextureId());
                    if (colorIndex < 0) colorIndex = 15;
                    if (shadow) colorIndex += 16;
                    int realColor = colorCode[colorIndex];
                    GlStateManager.color((realColor >> 16 & 0xFF) / 255F, (realColor >> 8 & 0xFF) / 255F, (realColor & 0xFF) / 255F, alpha);
                } else if (colorIndex == 21) {
                    GlStateManager.color(red, green, blue, alpha);
                    GlStateManager.bindTexture(texture.getGlTextureId());
                }

                i++;
            } else if (character < charMap.size()) {
                drawChar(charMap.get(character), (float) x, (float) y);
                x += charMap.get(character).width - 8;
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        return (float) (x / 2);


    }

    @Override
    public float getStringWidth(String text) {

        if (text == null) return 0;
        text = processString(text);

        int width = 0;

        for (int i = 0; i < text.length(); i++) {
            final char character = text.charAt(i);

            if (character == '\247') i++;
            else if (character < charMap.size()) width += charMap.get(character).width - 8;
        }
        return width / 2f;
    }

    @Override
    public float drawStringWithShadow(String name, float x, float y, int color) {
        return drawStringWithShadow(name, (double) x, y, color);
    }

    @Override
    public void drawStringWithShadow(String name, float x, float y, Color color) {
        drawStringWithShadow(name, x, y, color.getRGB());
    }

    @Override
    public float drawCenteredString(String name, float x, float y, int color) {
        return drawCenteredString(name, (double) x, y, color);
    }

    @Override
    public float drawCenteredStringWithShadow(String name, float x, float y, int color) {
        return drawCenteredStringWithShadow(name, (double) x, y, color);
    }

    @Override
    public void drawCenteredString(String name, float x, float y, Color color) {
        drawCenteredString(name, x, y, color.getRGB());
    }

    @Override
    public float drawString(String text, float x, float y, int color, boolean shadow) {
        return drawString(text, (double) x, y, color, shadow);
    }

    @Override
    public void drawString(String name, float x, float y, Color color) {
        drawString(name, x, y, color.getRGB());
    }

    @Override
    public float drawString(String name, float x, float y, int color) {
        return drawString(name, (double) x, y, color);
    }

    @Override
    public int getHeight() {
        return getStringHeight();
    }


    private String processString(String text) {
        final StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) if ((c < 50000 || c > 60000) && c != 9917) sb.append(c);

        return sb.toString();
    }

    public void drawCenteredTextScaled(final String text, final int givenX, final int givenY, final int color, final double givenScale) {
        GL11.glPushMatrix();
        GL11.glTranslated(givenX, givenY, 0.0);
        GL11.glScaled(givenScale, givenScale, givenScale);
        this.drawCenteredString(text, 0.0f, 0.0f, color);
        GL11.glPopMatrix();
    }

    @SuppressWarnings("unused")
    public static class MCFont extends CFontRenderer {
        public MCFont() {
            super(null);
        }

        @Override
        public float drawString(String text, double x, double y, int color) {
            return Minecraft.getMinecraft().fontRendererObj.drawString(text, (int) x + 2, (int) y, color);
        }

        @Override
        public float getStringWidth(String text) {
            return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
        }

        @Override
        public float drawStringWithShadow(String text, double x, double y, int color) {
            return Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, (float) x + 2, (float) y, color);
        }

        @Override
        public int getStringHeight() {
            return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
        }
    }
}


