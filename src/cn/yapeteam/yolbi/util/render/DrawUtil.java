package cn.yapeteam.yolbi.util.render;

import cn.yapeteam.yolbi.util.IMinecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class DrawUtil implements IMinecraft {

    public static void drawGradientVerticalRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawGradientSideRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;

        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawDiagonalGradient(double left, double top, double right, double bottom, int startColor, int endColor, DiagonalType diagonal) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;

        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);

        if(diagonal == DiagonalType.RIGHT_TOP) {
            worldrenderer.pos(right, top, 0).color(f5, f6, f7, f4).endVertex();
        } else {
            worldrenderer.pos(right, top, 0).color(f1, f2, f3, f).endVertex();
        }

        if(diagonal == DiagonalType.LEFT_TOP) {
            worldrenderer.pos(left, top, 0).color(f5, f6, f7, f4).endVertex();
        } else {
            worldrenderer.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        }

        if(diagonal == DiagonalType.LEFT_BOTTOM) {
            worldrenderer.pos(left, bottom, 0).color(f5, f6, f7, f4).endVertex();
        } else {
            worldrenderer.pos(left, bottom, 0).color(f1, f2, f3, f).endVertex();
        }

        if(diagonal == DiagonalType.RIGHT_BOTTOM) {
            worldrenderer.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        } else {
            worldrenderer.pos(right, bottom, 0).color(f1, f2, f3, f).endVertex();
        }

        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void renderTriangle(double startX, double startY, int color) {
        double endX = startX + 6;

        Gui.drawRect(startX, startY, endX, startY + 0.5, color);
        Gui.drawRect(startX + 0.5, startY + 0.5, endX - 0.5, startY + 1, color);
        Gui.drawRect(startX + 1, startY + 1, endX - 1, startY + 1.5, color);
        Gui.drawRect(startX + 1.5, startY + 1.5, endX - 1.5, startY + 2, color);
        Gui.drawRect(startX + 2, startY + 2, endX - 2, startY + 2.5, color);
        Gui.drawRect(startX + 2.5, startY + 2.5, endX - 2.5, startY + 3, color);
    }

    public static void drawHead(ResourceLocation skin, int x, int y, int width, int height) {
        try {
            mc.getTextureManager().bindTexture(skin);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height, 64F, 64F);
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        try {
            mc.getTextureManager().bindTexture(image);
            GL11.glEnable(GL11.GL_BLEND);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void renderMainMenuBackground(GuiScreen screen, ScaledResolution sr) {
        int topColor = new Color(255, 161, 238).getRGB();
        int bottomColor = new Color(134, 164, 255).getRGB();

        screen.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), topColor, bottomColor);
    }

    public enum DiagonalType {
        LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM
    }
}