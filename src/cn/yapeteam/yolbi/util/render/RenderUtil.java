package cn.yapeteam.yolbi.util.render;

import cn.yapeteam.yolbi.util.render.gaussianblur.GaussianFilter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class RenderUtil {

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }

    public static void drawRect2(double x, double y, double width, double height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    public static void prepareBoxRender(float lineWidth, double red, double green, double blue, double alpha) {
        GL11.glBlendFunc(770, 771);
        glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDepthMask(false);

        GL11.glColor4d(red, green, blue, alpha);
    }
    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }


    public static void color(int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
    }

    public static void drawCircle(double x, double y, double radius, int c) {
        float alpha = (float) (c >> 24 & 255) / 255.0f;
        float red = (float) (c >> 16 & 255) / 255.0f;
        float green = (float) (c >> 8 & 255) / 255.0f;
        float blue = (float) (c & 255) / 255.0f;
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
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(9);
        int i = 0;
        while (i <= 360) {
            GL11.glVertex2d(
                    x + Math.sin((double) i * 3.141526 / 180.0) * radius,
                    y + Math.cos((double) i * 3.141526 / 180.0) * radius);
            ++i;
        }

        GL11.glEnd();
        if (texture) {
            GL11.glEnable(3553);
        }
        if (!line) {
            GL11.glDisable(2848);
        }
        if (!blend) {
            GL11.glDisable(3042);
        }
    }

    public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
        enableGL2D();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        color(topColor);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        color(bottomColor);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        disableGL2D();
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float) (col1 >> 24 & 255) / 255.0f;
        float f1 = (float) (col1 >> 16 & 255) / 255.0f;
        float f2 = (float) (col1 >> 8 & 255) / 255.0f;
        float f3 = (float) (col1 & 255) / 255.0f;
        float f4 = (float) (col2 >> 24 & 255) / 255.0f;
        float f5 = (float) (col2 >> 16 & 255) / 255.0f;
        float f6 = (float) (col2 >> 8 & 255) / 255.0f;
        float f7 = (float) (col2 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void renderEntityBox(RenderManager rm, float partialTicks, Entity entity) {
        AxisAlignedBB bb = entity.getEntityBoundingBox();

        double posX = interpolate(entity.posX, entity.lastTickPosX, partialTicks);
        double posY = interpolate(entity.posY, entity.lastTickPosY, partialTicks);
        double posZ = interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);

        RenderGlobal.func_181561_a(
                new AxisAlignedBB(
                        bb.minX - 0.05 - entity.posX + (posX - rm.renderPosX),
                        bb.minY - 0.05 - entity.posY + (posY - rm.renderPosY),
                        bb.minZ - 0.05 - entity.posZ + (posZ - rm.renderPosZ),
                        bb.maxX + 0.05 - entity.posX + (posX - rm.renderPosX),
                        bb.maxY + 0.1 - entity.posY + (posY - rm.renderPosY),
                        bb.maxZ + 0.05 - entity.posZ + (posZ - rm.renderPosZ)
                )
        );
    }

    public static void renderCustomPlayerBox(RenderManager rm, float partialTicks, double x, double y, double z) {
        renderCustomPlayerBox(rm, partialTicks, x, y, z, x, y, z);
    }

    public static void renderCustomPlayerBox(RenderManager rm, float partialTicks, double x, double y, double z, double lastX, double lastY, double lastZ) {
        AxisAlignedBB bb = new AxisAlignedBB(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3);

        double posX = interpolate(x, lastX, partialTicks);
        double posY = interpolate(y, lastY, partialTicks);
        double posZ = interpolate(z, lastZ, partialTicks);

        RenderGlobal.func_181561_a(
                new AxisAlignedBB(
                        bb.minX - 0.05 - x + (posX - rm.renderPosX),
                        bb.minY - 0.05 - y + (posY - rm.renderPosY),
                        bb.minZ - 0.05 - z + (posZ - rm.renderPosZ),
                        bb.maxX + 0.05 - x + (posX - rm.renderPosX),
                        bb.maxY + 0.1 - y + (posY - rm.renderPosY),
                        bb.maxZ + 0.05 - z + (posZ - rm.renderPosZ)
                )
        );
    }

    public static void stopBoxRender() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4d(1, 1, 1, 1);
    }

    public static double interpolate(double current, double old, double scale) {
        return (old + (current - old) * scale);
    }

    private static final Map<Integer, Integer> shadowCache = new HashMap<>();

    public static void drawBloomShadow(float x, float y, float width, float height, int blurRadius, Color color) {
        glPushMatrix();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        float _X = x - 0.25f;
        float _Y = y + 0.25f;

        int identifier = (int) (width * height + width + color.hashCode() * blurRadius + blurRadius);

        glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL_CULL_FACE);
        glEnable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableBlend();

        if (shadowCache.containsKey(identifier)) {
            GlStateManager.bindTexture(shadowCache.get(identifier));
        } else {
            if (width <= 0) width = 1;
            if (height <= 0) height = 1;
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics g = original.getGraphics();
            g.setColor(color);
            g.fillRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2));
            g.dispose();
            GaussianFilter op = new GaussianFilter(blurRadius);
            BufferedImage blurred = op.filter(original, null);
            shadowCache.put(identifier, TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false));
        }

        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0); // top left
        GL11.glVertex2f(_X, _Y);

        GL11.glTexCoord2f(0, 1); // bottom left
        GL11.glVertex2f(_X, _Y + height);

        GL11.glTexCoord2f(1, 1); // bottom right
        GL11.glVertex2f(_X + width, _Y + height);

        GL11.glTexCoord2f(1, 0); // top right
        GL11.glVertex2f(_X + width, _Y);
        GL11.glEnd();

        enableTexture2D();
        disableBlend();
        GlStateManager.resetColor();

        glEnable(GL_CULL_FACE);
        glPopMatrix();
    }
    public static void drawFastRoundedRect(final float left, final float top, final float right, final float bottom, final float radius, final int color) {
        final float f2 = (color >> 24 & 0xFF) / 255.0f;
        final float f3 = (color >> 16 & 0xFF) / 255.0f;
        final float f4 = (color >> 8 & 0xFF) / 255.0f;
        final float f5 = (color & 0xFF) / 255.0f;
        glDisable(2884);
        glDisable(3553);
        glEnable(3042);
        glBlendFunc(770, 771);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glColor4f(f3, f4, f5, f2);
        glBegin(5);
        glVertex2f(left + radius, top);
        glVertex2f(left + radius, bottom);
        glVertex2f(right - radius, top);
        glVertex2f(right - radius, bottom);
        glEnd();
        glBegin(5);
        glVertex2f(left, top + radius);
        glVertex2f(left + radius, top + radius);
        glVertex2f(left, bottom - radius);
        glVertex2f(left + radius, bottom - radius);
        glEnd();
        glBegin(5);
        glVertex2f(right, top + radius);
        glVertex2f(right - radius, top + radius);
        glVertex2f(right, bottom - radius);
        glVertex2f(right - radius, bottom - radius);
        glEnd();
        glBegin(6);
        float f6 = right - radius;
        float f7 = top + radius;
        glVertex2f(f6, f7);
        int j;
        for (j = 0; j <= 18; ++j)
            glVertex2f((float) (f6 + radius * Math.cos(Math.toRadians(j * 5.0f))), (float) (f7 - radius * Math.sin(Math.toRadians(j * 5.0f))));
        glEnd();
        glBegin(6);
        f6 = left + radius;
        f7 = top + radius;
        glVertex2f(f6, f7);
        for (j = 0; j <= 18; ++j)
            glVertex2f((float) (f6 - radius * Math.cos(Math.toRadians(j * 5.0f))), (float) (f7 - radius * Math.sin(Math.toRadians(j * 5.0f))));
        glEnd();
        glBegin(6);
        f6 = left + radius;
        f7 = bottom - radius;
        glVertex2f(f6, f7);
        for (j = 0; j <= 18; ++j)
            glVertex2f((float) (f6 - radius * Math.cos(Math.toRadians(j * 5.0f))), (float) (f7 + radius * Math.sin(Math.toRadians(j * 5.0f))));
        glEnd();
        glBegin(6);
        f6 = right - radius;
        f7 = bottom - radius;
        glVertex2f(f6, f7);
        for (j = 0; j <= 18; ++j)
            glVertex2f((float) (f6 + radius * Math.cos(Math.toRadians(j * 5.0f))), (float) (f7 + radius * Math.sin(Math.toRadians(j * 5.0f))));
        glEnd();
        glEnable(3553);
        glEnable(2884);
        glDisable(3042);
        enableTexture2D();
        disableBlend();
    }

    public static void drawFastRoundedRect2(float x, float y, float width, float height, float radius, int color) {
        drawFastRoundedRect(x, y, x + width, y + height, radius, color);
    }

    public static void circle(final float x, final float y, final float radius, final int fill) {
        arc(x, y, 0.0f, 360.0f, radius, fill);
    }

    public static void arc(final float x, final float y, final float start, final float end, final float radius,
                           final int color) {
        arcEllipse(x, y, start, end, radius, radius, color);
    }

    public static void arc(final float x, final float y, final float start, final float end, final float radius,
                           final Color color) {
        arcEllipse(x, y, start, end, radius, radius, color);
    }

    public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h,
                                  final int color) {
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        float temp;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        final float var11 = (color >> 24 & 0xFF) / 255.0f;
        final float var12 = (color >> 16 & 0xFF) / 255.0f;
        final float var13 = (color >> 8 & 0xFF) / 255.0f;
        final float var14 = (color & 0xFF) / 255.0f;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var12, var13, var14, var11);
        if (var11 > 0.5f) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GL11.glDisable(2848);
        }
        GL11.glBegin(6);
        for (float i = end; i >= start; i -= 4.0f) {
            final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
            final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
            GL11.glVertex2f(x + ldx, y + ldy);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h,
                                  final Color color) {
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        float temp;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        final Tessellator var9 = Tessellator.getInstance();
        final WorldRenderer var10 = var9.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
        if (color.getAlpha() > 0.5f) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GL11.glDisable(2848);
        }
        GL11.glBegin(6);
        for (float i = end; i >= start; i -= 4.0f) {
            final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
            final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
            GL11.glVertex2f(x + ldx, y + ldy);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}