package cn.yapeteam.yolbi.render.shader.impl;

import cn.yapeteam.yolbi.render.shader.Shader;

import java.util.Arrays;

public class ShaderRoundedRect extends Shader {
    private final float radius;
    private final int color;

    public ShaderRoundedRect(float width, float height, float radius, int color, int level, boolean antiAlias) {
        this.identifier = Arrays.deepHashCode(new Object[]{width, height, radius, color, level, antiAlias});
        this.level = level;
        this.width = width * level;
        this.height = height * level;
        this.radius = radius * level;
        this.color = color;
        this.antiAlias = antiAlias;
    }

    @Override
    public int dispose(float relativeX, float relativeY, float screenWidth, float screenHeight, int pixel) {
        float radius2 = radius * radius;
        float a, a2, b, b2;
        if (relativeX <= radius) {
            a = relativeX - radius;
            a2 = a * a;
            b = relativeY - radius;
            b2 = b * b;
            if (relativeY <= radius) {
                if (a2 + b2 >= radius2)
                    return 0;
            }
            if (relativeY >= height - radius) {
                b = relativeY - (height - radius);
                b2 = b * b;
                if (a2 + b2 >= radius2)
                    return 0;
            }
        }
        if (relativeX >= width - radius) {
            a = relativeX - (width - radius);
            a2 = a * a;
            b = relativeY - radius;
            b2 = b * b;
            if (relativeY <= radius) {
                if (a2 + b2 >= radius2)
                    return 0;
            }
            if (relativeY >= height - radius) {
                a = relativeX - (width - radius);
                a2 = a * a;
                b = relativeY - (height - radius);
                b2 = b * b;
                if (a2 + b2 >= radius2)
                    return 0;
            }
        }
        return color;
    }
}
