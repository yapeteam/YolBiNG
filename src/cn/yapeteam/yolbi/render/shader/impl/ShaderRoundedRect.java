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

    @SuppressWarnings("DuplicatedCode")
    @Override
    public int dispose(float relativeX, float relativeY, float screenWidth, float screenHeight, int pixel) {
        float radius2 = radius * radius;
        float left = relativeX - radius;
        float right = width - relativeX - radius;
        float top = relativeY - radius;
        float bottom = height - relativeY - radius;

        if (left <= 0 && top <= 0 && left * left + top * top >= radius2) {
            return 0;
        }
        if (right <= 0 && top <= 0 && right * right + top * top >= radius2) {
            return 0;
        }
        if (left <= 0 && bottom <= 0 && left * left + bottom * bottom >= radius2) {
            return 0;
        }
        if (right <= 0 && bottom <= 0 && right * right + bottom * bottom >= radius2) {
            return 0;
        }

        return color;
    }
}
