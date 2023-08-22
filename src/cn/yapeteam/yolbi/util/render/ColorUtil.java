package cn.yapeteam.yolbi.util.render;

import java.awt.*;

public class ColorUtil {
    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float) c.getRed();
        float g = 0.003921569f * (float) c.getGreen();
        float b = 0.003921569f * (float) c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    public static final int buttonHoveredColor = new Color(255, 255, 255).getRGB();

    public static int getColor(Color color1, Color color2, long ms, int offset) {
        double scale = (((System.currentTimeMillis() + offset) % ms) / (double) ms) * 2;
        double finalScale = scale > 1 ? 2 - scale : scale;

        return getGradient(color1, color2, finalScale).getRGB();
    }

    public static int getColor(Color color1, Color color2, Color color3, long ms, int offset) {
        double scale = (((System.currentTimeMillis() + offset) % ms) / (double) ms) * 3;

        if (scale > 2) {
            return getGradient(color3, color1, scale - 2).getRGB();
        } else if (scale > 1) {
            return getGradient(color2, color3, scale - 1).getRGB();
        } else {
            return getGradient(color1, color2, scale).getRGB();
        }
    }

    public static Color intToColor(int color) {
        Color c1 = new Color(color);
        return new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), color >> 24 & 255);
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) (opacity * 255))));
    }

    public static Color getGradient(Color color1, Color color2, double scale) {
        scale = Math.max(0, Math.min(1, scale));

        return new Color((int) (color1.getRed() + (color2.getRed() - color1.getRed()) * scale),
                (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * scale),
                (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * scale));
    }

    public static int getRainbow(long ms, int offset, float saturation, float brightness) {
        float scale = ((System.currentTimeMillis() + offset) % ms) / (float) ms;

        return Color.HSBtoRGB(scale, saturation, brightness);
    }
}