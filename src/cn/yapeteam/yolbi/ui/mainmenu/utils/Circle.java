package cn.yapeteam.yolbi.ui.mainmenu.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Getter
@Setter
public class Circle {
    public double topRadius;
    public double speed;
    private final Flag flag;

    public double progress;
    public double lastProgress;
    public boolean complete;


    public Circle(double rad, double speed, Flag flag) {
        topRadius = rad;
        this.speed = speed;
        this.flag = flag;
    }

    public void runCircle() {
        if (complete) return;
        lastProgress = progress;
        if (getFlag().get() && progress > topRadius * 0.67)
            return;
        progress += (topRadius - progress) / (speed) + 0.01;
        if (progress >= topRadius) {
            complete = true;
        }
    }

    public void drawCircle(float x, float y) {
        float progress = (float) (this.progress * Minecraft.getMinecraft().timer.renderPartialTicks + (lastProgress * (1.0f - Minecraft.getMinecraft().timer.renderPartialTicks)));
        if (!complete)
            drawBorderedCircle(x, y, progress, new Color(0, 0, 0, (1 - Math.min(1f, Math.max(0f, (float) (progress / topRadius)))) / 2).getRGB());
    }

    public void drawCircle(float x, float y,Color color) {
        float progress = (float) (this.progress * Minecraft.getMinecraft().timer.renderPartialTicks + (lastProgress * (1.0f - Minecraft.getMinecraft().timer.renderPartialTicks)));
        if (!complete)
            drawBorderedCircle(x, y, progress, new Color(color.getRed()/255, color.getGreen()/255, color.getBlue()/255, (1 - Math.min(1f, Math.max(0f, (float) (progress / topRadius)))) / 2).getRGB());
    }

    public static void drawBorderedCircle(double x, double y, float radius, int color) {
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glScalef(0.1f, 0.1f, 0.1f);
        drawCircle(x * 10, y * 10, radius * 10.0f, color);
        GL11.glScalef(10.0f, 10.0f, 10.0f);
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
    }

    public static void drawCircle(double x, double y, float radius, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0f;
        float red = (float) (color >> 16 & 255) / 255.0f;
        float green = (float) (color >> 8 & 255) / 255.0f;
        float blue = (float) (color & 255) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(9);
        int i = 0;
        while (i <= 360) {
            GL11.glVertex2d(x + Math.sin((double) i * 3.141526 / 180.0) * (double) radius, y + Math.cos((double) i * 3.141526 / 180.0) * (double) radius);
            ++i;
        }
        GL11.glEnd();
    }
}
