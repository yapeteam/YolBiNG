package cn.yapeteam.yolbi.ui.noti;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.impl.visual.ClientTheme;
import cn.yapeteam.yolbi.util.IMinecraft;
import cn.yapeteam.yolbi.util.render.animation.Animation;
import cn.yapeteam.yolbi.util.render.animation.Easing;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SuppressWarnings({"DuplicatedCode", "SameParameterValue"})
public class Notification implements IMinecraft {
    @Getter
    private final String text;
    @Setter
    private int index = 0;
    @Getter
    @Setter
    private float targetX, targetY;
    @Getter
    private float currentX, currentY;
    public float width = 70;
    @Getter
    private static final int height = 15;
    public static final int delay = 3000;
    private static final int leaveTime = 200;
    @Getter
    @Setter
    private int leftTime = delay;

    public Notification(String text) {
        this.text = text;
        beginTime = System.currentTimeMillis();
        animationShp = new Animation(Easing.EASE_OUT_QUART, leaveTime);
        animationX = new Animation(Easing.EASE_IN_OUT_BACK, leaveTime);
        animationY = new Animation(Easing.EASE_IN_CUBIC, leaveTime);
        animationShp.setValue(height / 2f);
        animationShp.reset();
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
        animationX.setValue(currentX);
        animationX.reset();
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
        animationY.setValue(currentY);
        animationY.reset();
    }

    public float getWidth() {
        AbstractFontRenderer font = YolBi.instance.getFontManager().getPingFang16();
        width = font.getStringWidth(text) + 6;
        return width;
    }

    private final long beginTime;

    private final Animation animationShp, animationX, animationY;

    public void render() {
        ScaledResolution sr = new ScaledResolution(mc);
        leftTime = (int) (delay - (System.currentTimeMillis() - beginTime));
        if (leftTime <= leaveTime) setTargetX(sr.getScaledWidth() + 2);
        animationShp.run(leftTime > leaveTime ? 0 : height / 2f);
        animationX.run(targetX);
        animationY.run(targetY);
        currentX = (float) animationX.getValue();
        currentY = (float) animationY.getValue();

        AbstractFontRenderer font = YolBi.instance.getFontManager().getPingFang16();
        width = font.getStringWidth(text) + 6;
        drawSpcBloomRect(currentX, currentY, width, height, height / 2f, animationShp.getValue(), new Color(0, 0, 0, 100).getRGB());
        GlStateManager.color(1, 1, 1, 1);
        int alpha = (int) (Math.max(Math.min(255 * (leftTime > leaveTime ? animationShp.getProgress() : 1 - animationShp.getProgress()), 255), 0));
        ClientTheme clientTheme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);
        int[] colors = new int[text.length()];
        for (int i = 0; i < colors.length; i++) {
            Color color = new Color(clientTheme.getColor((i + index * 10) * 100));
            colors[i] = new Color(color.getRed(), color.getGreen(), color.getGreen(), alpha).getRGB();
        }
        ((FontRenderer) font).drawStringWithColors(text, currentX + (width - font.getStringWidth(text)) / 2f, currentY + (height - font.getStringHeight(text)) / 2f, colors, false);
    }

    private double getCircle(double radius, double distance) {
        return Math.sqrt(radius * radius - distance * distance);
    }

    private void drawSpcBloomRect(float x, float y, float width, float height, float radius, double distance, int color) {
        double h = getCircle(radius, distance);
        double x1 = x + distance, y1 = y - h + height / 2f,
                x2 = x + distance + width, y2 = y - h + height / 2f,
                x3 = x - distance, y3 = y + h + height / 2f,
                x4 = x - distance + width, y4 = y + h + height / 2f;
        float alpha = (float) (color >> 24 & 255) / 255.0f;
        float red = (float) (color >> 16 & 255) / 255.0f;
        float green = (float) (color >> 8 & 255) / 255.0f;
        float blue = (float) (color & 255) / 255.0f;
        boolean blend = GL11.glIsEnabled(3042);
        boolean line = GL11.glIsEnabled(2848);
        boolean texture = GL11.glIsEnabled(3553);
        if (!blend) {
            GL11.glEnable(3042);
        }
        if (!line) {
            GL11.glEnable(2848);
        }
        if (texture) {
            GL11.glDisable(3553);
        }
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glColor4f(red, green, blue, alpha);

        GL11.glVertex2d(x3, y3);
        GL11.glVertex2d(x4, y4);

        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x1, y1);

        GL11.glEnd();
        if (texture) GL11.glEnable(3553);
        if (!line) GL11.glDisable(2848);
        if (!blend) GL11.glDisable(3042);
        GL11.glColor4f(1, 1, 1, 1);
    }
}
