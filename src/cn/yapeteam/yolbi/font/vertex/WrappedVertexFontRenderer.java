package cn.yapeteam.yolbi.font.vertex;

import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class WrappedVertexFontRenderer extends FontRenderer implements AbstractFontRenderer {
    public final int baseSize;
    public final VertexFontRenderer vfr;
    public final int newSize;
    private final float scale;

    public WrappedVertexFontRenderer(VertexFontRenderer vfr, int size) {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().getTextureManager(), false);
        this.baseSize = vfr.getFont().getSize();
        this.vfr = vfr;
        this.newSize = size;
        this.scale = (float) this.newSize / (float) this.baseSize;
        this.FONT_HEIGHT = (int) ((float) vfr.fontHeight * this.scale);
    }

    public float drawString(String string, int x, int y, int color) {
        return this.drawString(string, (float) x, (float) y, color);
    }

    public float drawString(String string, float x, float y, int color) {
        GL11.glPushMatrix();
        this.vfr.preGlHint();
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glScalef(this.scale, this.scale, this.scale);
        this.vfr.drawString(string, 0.0F, 0.0F, color, false);
        this.vfr.postGlHint();
        GL11.glPopMatrix();
        return this.vfr.getStringWidth(string);
    }

    @Override
    public int getHeight() {
        return FONT_HEIGHT;
    }

    public float drawStringWithShadowShifted(String text, float x, float y, int color, int shadowAlpha) {
        this.drawString(text, x + 1.0F, y + 1.0F, (new Color(0, 0, 0, shadowAlpha)).getRGB());
        return this.drawString(text, x, y, color);
    }

    @Override
    public float drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x + 0.5F, y + 0.5F, ColorUtil.reAlpha(Color.BLACK.getRGB(), 0.6F));
        return this.drawString(text, x, y, color);
    }

    @Override
    public void drawStringWithShadow(String name, float x, float y, Color color) {
        drawStringWithShadow(name, x, y, color.getRGB());
    }

    public float drawStringWithShadow(String text, float x, float y, int color, int shadowAlpha) {
        this.drawString(text, x + 0.5F, y + 0.5F, (new Color(0, 0, 0, shadowAlpha)).getRGB());
        return this.drawString(text, x, y, color);
    }

    @Override
    public float getCharWidth(char c) {
        return this.getStringWidth(Character.toString(c));
    }

    @Override
    public float getStringWidth(String string) {
        return (int) ((float) this.vfr.getStringWidth(string) * this.scale);
    }

    public int drawStringWithColor(String text, float x, float y, int color, int alpha) {
        text = "§r" + text;
        float len = -1.0F;
        String[] array = text.split("§");

        for (String s : array) {
            String str = s;
            if (str.length() >= 1) {
                switch (str.charAt(0)) {
                    case '0':
                        color = (new Color(0, 0, 0)).getRGB();
                        break;
                    case '1':
                        color = (new Color(0, 0, 170)).getRGB();
                        break;
                    case '2':
                        color = (new Color(0, 170, 0)).getRGB();
                        break;
                    case '3':
                        color = (new Color(0, 170, 170)).getRGB();
                        break;
                    case '4':
                        color = (new Color(170, 0, 0)).getRGB();
                        break;
                    case '5':
                        color = (new Color(170, 0, 170)).getRGB();
                        break;
                    case '6':
                        color = (new Color(255, 170, 0)).getRGB();
                        break;
                    case '7':
                        color = (new Color(170, 170, 170)).getRGB();
                        break;
                    case '8':
                        color = (new Color(85, 85, 85)).getRGB();
                        break;
                    case '9':
                        color = (new Color(85, 85, 255)).getRGB();
                    case ':':
                    case ';':
                    case '<':
                    case '=':
                    case '>':
                    case '?':
                    case '@':
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'H':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'S':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    case 'Z':
                    case '[':
                    case '\\':
                    case ']':
                    case '^':
                    case '_':
                    case '`':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'm':
                    case 'n':
                    case 'o':
                    case 'p':
                    case 'q':
                    default:
                        break;
                    case 'a':
                        color = (new Color(85, 255, 85)).getRGB();
                        break;
                    case 'b':
                        color = (new Color(85, 255, 255)).getRGB();
                        break;
                    case 'c':
                        color = (new Color(255, 85, 85)).getRGB();
                        break;
                    case 'd':
                        color = (new Color(255, 85, 255)).getRGB();
                        break;
                    case 'e':
                        color = (new Color(255, 255, 85)).getRGB();
                        break;
                    case 'f':
                        color = (new Color(255, 255, 255)).getRGB();
                        break;
                    case 'r':
                        color = (new Color(255, 255, 255)).getRGB();
                }

                Color col = new Color(color);
                str = str.substring(1);
                this.drawString(str, x + len + 0.5F, y + 0.5F, Color.BLACK.getRGB());
                this.drawString(str, x + len, y, this.getColor(col.getRed(), col.getGreen(), col.getBlue(), alpha));
                len += (float) (this.getStringWidth(str) + 1);
            }
        }

        return (int) len;
    }

    public int drawStringWithColor(String text, float x, float y, int color) {
        text = "§r" + text;
        float len = -1.0F;
        String[] array = text.split("§");

        for (String s : array) {
            String str = s;
            if (str.length() >= 1) {
                switch (str.charAt(0)) {
                    case '0':
                        color = (new Color(0, 0, 0)).getRGB();
                        break;
                    case '1':
                        color = (new Color(0, 0, 170)).getRGB();
                        break;
                    case '2':
                        color = (new Color(0, 170, 0)).getRGB();
                        break;
                    case '3':
                        color = (new Color(0, 170, 170)).getRGB();
                        break;
                    case '4':
                        color = (new Color(170, 0, 0)).getRGB();
                        break;
                    case '5':
                        color = (new Color(170, 0, 170)).getRGB();
                        break;
                    case '6':
                        color = (new Color(255, 170, 0)).getRGB();
                        break;
                    case '7':
                        color = (new Color(170, 170, 170)).getRGB();
                        break;
                    case '8':
                        color = (new Color(85, 85, 85)).getRGB();
                        break;
                    case '9':
                        color = (new Color(85, 85, 255)).getRGB();
                    case ':':
                    case ';':
                    case '<':
                    case '=':
                    case '>':
                    case '?':
                    case '@':
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'H':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'S':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    case 'Z':
                    case '[':
                    case '\\':
                    case ']':
                    case '^':
                    case '_':
                    case '`':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'm':
                    case 'n':
                    case 'o':
                    case 'p':
                    case 'q':
                    default:
                        break;
                    case 'a':
                        color = (new Color(85, 255, 85)).getRGB();
                        break;
                    case 'b':
                        color = (new Color(85, 255, 255)).getRGB();
                        break;
                    case 'c':
                        color = (new Color(255, 85, 85)).getRGB();
                        break;
                    case 'd':
                        color = (new Color(255, 85, 255)).getRGB();
                        break;
                    case 'e':
                        color = (new Color(255, 255, 85)).getRGB();
                        break;
                    case 'f':
                        color = (new Color(255, 255, 255)).getRGB();
                        break;
                    case 'r':
                        color = (new Color(255, 255, 255)).getRGB();
                }

                str = str.substring(1);
                this.drawString(str, x + len, y, color);
                len += (float) (this.getStringWidth(str) + 1);
            }
        }

        return (int) len;
    }

    public int getColor(int brightness, int alpha) {
        return this.getColor(brightness, brightness, brightness, alpha);
    }

    public int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

    @Override
    public float drawCenteredString(String text, float x, float y, int color) {
        return this.drawString(text, x - (this.getStringWidth(text) / 2), y, color);
    }

    @Override
    public float drawCenteredStringWithShadow(String name, float x, float y, int color) {
        return this.drawString(name, x - (this.getStringWidth(name) / 2), y, color, true);
    }

    @Override
    public void drawCenteredString(String name, float x, float y, Color color) {
        this.drawString(name, x - (this.getStringWidth(name) / 2), y, color.getRGB());
    }

    @Override
    public void drawString(String name, float x, float y, Color color) {
        this.drawString(name, x, y, color.getRGB());
    }

    public void drawOutlinedString(String text, float x, float y, int borderColor, int color) {
        this.drawString(text, x - 0.5F, y, borderColor);
        this.drawString(text, x + 0.5F, y, borderColor);
        this.drawString(text, x, y - 0.5F, borderColor);
        this.drawString(text, x, y + 0.5F, borderColor);
        this.drawString(text, x, y, color);
    }

    public void drawCenterOutlinedString(String text, float x, float y, int borderColor, int color) {
        this.drawString(text, x - (float) (this.getStringWidth(text) / 2) - 0.5F, y, borderColor);
        this.drawString(text, x - (float) (this.getStringWidth(text) / 2) + 0.5F, y, borderColor);
        this.drawString(text, x - (float) (this.getStringWidth(text) / 2), y - 0.5F, borderColor);
        this.drawString(text, x - (float) (this.getStringWidth(text) / 2), y + 0.5F, borderColor);
        this.drawString(text, x - (float) (this.getStringWidth(text) / 2), y, color);
    }

    public float Ex(String text, double x2, double y2, int color) {
        float shadowWidth = (float) this.drawString(text, (float) (x2 + 0.7), (float) (y2 + 0.6), color, true);
        return Math.max(shadowWidth, (float) this.drawString(text, (float) x2, (float) y2, color, false));
    }
}
