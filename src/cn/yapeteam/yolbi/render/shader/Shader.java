package cn.yapeteam.yolbi.render.shader;

public abstract class Shader {
    public int x, y, width, height;
    public int identifier = -1;
    public int level = 1;
    public boolean antiAlias = false;

    public abstract int dispose(float relativeX, float relativeY, float screenWidth, float screenHeight, int pixel);

    public int getRealWidth() {
        return width / level;
    }

    public int getRealHeight() {
        return height / level;
    }
}
