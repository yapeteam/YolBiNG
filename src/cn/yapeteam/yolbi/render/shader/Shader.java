package cn.yapeteam.yolbi.render.shader;

public abstract class Shader {
    public float x, y, width, height;
    public int identifier = -1;
    public int level = 1;
    public boolean antiAlias = false;

    public abstract int dispose(float relativeX, float relativeY, float screenWidth, float screenHeight, int pixel);

    public float getRealWidth() {
        return width / level;
    }

    public float getRealHeight() {
        return height / level;
    }
}
