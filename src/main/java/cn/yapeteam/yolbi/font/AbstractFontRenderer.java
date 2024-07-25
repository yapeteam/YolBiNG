package cn.yapeteam.yolbi.font;

import java.awt.*;

public interface AbstractFontRenderer {
    float getStringWidth(String text);

    float getStringHeight(String s);

    float drawStringWithShadow(String name, float x, float y, int color);

    void drawStringWithShadow(String name, float x, float y, Color color);

    float drawCenteredString(String name, float x, float y, int color);

    float drawCenteredStringWithShadow(String name, float x, float y, int color);

    void drawCenteredString(String name, float x, float y, Color color);

    float drawString(String text, float x, float y, int color, boolean shadow);

    void drawString(String name, float x, float y, Color color);

    float drawString(String name, float x, float y, int color);

    float getHeight();
}
