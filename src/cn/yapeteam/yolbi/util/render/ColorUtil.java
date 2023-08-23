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

    public static int getColor(float hueoffset, float saturation, float brightness ) {
        float speed = 4500f;
        float hue = System.currentTimeMillis() % speed / speed;
        return Color.HSBtoRGB(hue - hueoffset / 54, saturation, brightness);
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

    public static int getOppositeColor(int color) {
        int R = bitChangeColor(color, 0);
        int G = bitChangeColor(color, 8);
        int B = bitChangeColor(color, 16);
        int A = bitChangeColor(color, 24);
        R = 255 - R;
        G = 255 - G;
        B = 255 - B;
        return R + (G << 8) + (B << 16) + (A << 24);
    }

    public static Color getOppositeColor(Color color) {
        return new Color(getOppositeColor(color.getRGB()));
    }


    private static int bitChangeColor(int color, int bitChange) {
        return (color >> bitChange) & 255;
    }

}