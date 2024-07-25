package cn.yapeteam.yolbi.render.shader;

public abstract class Shader {
    public final int identifier;
    public final float width, height;
    public final int color;
    public final int level;
    public final boolean antiAlias, multithreading;

    public Shader(float width, float height, int color, int identifier, int level, boolean antiAlias, boolean multithreading) {
        this.width = width * level;
        this.height = height * level;
        this.color = color;
        this.level = level;
        this.antiAlias = antiAlias;
        this.multithreading = multithreading;
        this.identifier = identifier;
    }

    public abstract int dispose(float relativeX, float relativeY, float screenWidth, float screenHeight, int pixel);

    public float getRealWidth() {
        return width / level;
    }

    public float getRealHeight() {
        return height / level;
    }
}
