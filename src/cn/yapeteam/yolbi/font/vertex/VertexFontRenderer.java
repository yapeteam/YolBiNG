package cn.yapeteam.yolbi.font.vertex;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.TickEvent;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.*;

public class VertexFontRenderer {
    private final Font font;
    private final FontMetrics fontMetrics;
    public final int fontHeight;
    private final Map<Character, VertexCache> charMap = new HashMap<>();

    public VertexFontRenderer(Font font) {
        this.font = font;
        this.fontMetrics = (new Canvas()).getFontMetrics(font);
        this.fontHeight = ((this.fontMetrics.getHeight() < 0 ? font.getSize() : this.fontMetrics.getHeight() + 3) - 8) / 2;

        Vestige.instance.getEventManager().register(this);
    }

    public void drawString(String text, float x, float y, int color) {
        this.drawString(text, x, y, color, true);
    }

    public void drawString(String text, float x, float y, int color, boolean matrix) {
        if (matrix) {
            GL11.glPushMatrix();
        }

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glTranslated(x, y, 0.0);
        RenderUtil.color(color);
        char[] var6 = text.toCharArray();

        for (char c : var6) {
            GL11.glTranslatef((float) this.drawChar(c), 0.0F, 0.0F);
        }

        if (matrix) {
            GL11.glPopMatrix();
        }
    }

    public int drawChar(char c) {
        if (this.charMap.containsKey(c)) {
            VertexCache vc = this.charMap.get(c);
            vc.render();
            return vc.getWidth();
        } else {
            String charAsString = String.valueOf(c);
            int list = GL11.glGenLists(1);
            int width = this.fontMetrics.stringWidth(charAsString);
            GL11.glNewList(list, 4865);
            drawAWTShape(this.font.createGlyphVector(new FontRenderContext(new AffineTransform(), true, false), charAsString).getOutline(0.0F, (float) this.fontMetrics.getAscent()), 0.5);
            GL11.glEndList();
            this.charMap.put(c, new VertexCache(c, list, width));
            return width;
        }
    }

    public static void drawAWTShape(Shape shape, double epsilon) {
        PathIterator path = shape.getPathIterator(new AffineTransform());
        Double[] cp = new Double[2];
        GLUtessellator tess = GLU.gluNewTess();
        tess.gluTessCallback(100100, TessCallback.INSTANCE);
        tess.gluTessCallback(100102, TessCallback.INSTANCE);
        tess.gluTessCallback(100101, TessCallback.INSTANCE);
        tess.gluTessCallback(100105, TessCallback.INSTANCE);
        switch (path.getWindingRule()) {
            case 0:
                tess.gluTessProperty(100140, 100130.0);
                break;
            case 1:
                tess.gluTessProperty(100140, 100131.0);
        }

        ArrayList<Double[]> pointsCache = new ArrayList<>();
        tess.gluTessBeginPolygon(null);

        for (; !path.isDone(); path.next()) {
            double[] segment = new double[6];
            int type = path.currentSegment(segment);
            Double[][] points;
            switch (type) {
                case 0:
                    tess.gluTessBeginContour();
                    pointsCache.add(new Double[]{segment[0], segment[1]});
                    cp[0] = segment[0];
                    cp[1] = segment[1];
                    break;
                case 1:
                    pointsCache.add(new Double[]{segment[0], segment[1]});
                    cp[0] = segment[0];
                    cp[1] = segment[1];
                    break;
                case 2:
                    points = getPointsOnCurve(new Double[][]{{cp[0], cp[1]}, {segment[0], segment[1]}, {segment[2], segment[3]}}, 10);
                    pointsCache.addAll(Arrays.asList(points));
                    cp[0] = segment[2];
                    cp[1] = segment[3];
                    break;
                case 3:
                    points = getPointsOnCurve(new Double[][]{{cp[0], cp[1]}, {segment[0], segment[1]}, {segment[2], segment[3]}, {segment[4], segment[5]}}, 10);
                    pointsCache.addAll(Arrays.asList(points));
                    cp[0] = segment[4];
                    cp[1] = segment[5];
                    break;
                case 4:
                    points = simplifyPoints(pointsCache.toArray(new Double[0][0]), epsilon);

                    for (Double[] point : points) {
                        tessVertex(tess, new double[]{point[0], point[1], 0.0, 0.0, 0.0, 0.0});
                    }

                    pointsCache.clear();
                    tess.gluTessEndContour();
            }
        }

        tess.gluEndPolygon();
        tess.gluDeleteTess();
    }

    public static void tessVertex(GLUtessellator tessellator, double[] coords) {
        tessellator.gluTessVertex(coords, 0, new VertexData(coords));
    }

    public int getStringWidth(String text) {
        return this.fontMetrics.stringWidth(text) / 2;
    }

    private int ticks = 0;

    @Listener
    public void onTick(TickEvent event) {
        if (ticks++ > 200) {
            ticks = 0;
            System.out.println("Font GC");
            gcTick();
        }
    }

    public void gcTick() {
        VertexCache[] var1 = this.charMap.values().toArray(new VertexCache[0]);

        for (VertexCache cache : var1) {
            if (cache.checkTimeNotUsed(30000L)) {
                cache.destroy();
                this.charMap.remove(cache.getChar());
            }
        }
    }

    public static Double[] lerp(Double[] a, Double[] b, double t) {
        return new Double[]{a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t};
    }

    public static Double[] calcCurvePoint(Double[][] points, double t) {
        ArrayList<Double[]> cPoints = new ArrayList<>();

        for (int i = 0; i < points.length - 1; ++i) {
            cPoints.add(lerp(points[i], points[i + 1], t));
        }

        return cPoints.size() == 1 ? cPoints.get(0) : calcCurvePoint(cPoints.toArray(new Double[0][0]), t);
    }

    public static Double[][] getPointsOnCurve(Double[][] points, int num) {
        ArrayList<Double[]> cPoints = new ArrayList<>();

        for (int i = 0; i < num; ++i) {
            double t = (double) i / ((double) num - 1.0);
            cPoints.add(calcCurvePoint(points, t));
        }

        return cPoints.toArray(new Double[0][0]);
    }

    public static Double[][] simplifyPoints(Double[][] points, double epsilon) {
        return simplifyPoints(points, epsilon, 0, points.length, new ArrayList<>());
    }

    public static double distanceSq(Double[] a, Double[] b) {
        return Math.pow(a[0] - b[0], 2.0) + Math.pow(a[1] - b[1], 2.0);
    }

    public static double distanceToSegmentSq(Double[] p, Double[] v, Double[] w) {
        double l2 = distanceSq(v, w);
        return l2 == 0.0 ? distanceSq(p, v) : distanceSq(p, lerp(v, w, glmClamp(((p[0] - v[0]) * (w[0] - v[0]) + (p[1] - v[1]) * (w[1] - v[1])) / l2, 0.0, 1.0)));
    }

    public static double glmClamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }


    public static Double[][] simplifyPoints(Double[][] points, double epsilon, int start, int end, ArrayList<Double[]> outPoints) {
        Double[] s = points[start];
        Double[] e = points[end - 1];
        double maxDistSq = 0.0;
        int maxNdx = 1;

        for (int i = start + 1; i < end - 1; ++i) {
            double distSq = distanceToSegmentSq(points[i], s, e);
            if (distSq > maxDistSq) {
                maxDistSq = distSq;
                maxNdx = i;
            }
        }

        if (Math.sqrt(maxDistSq) > epsilon) {
            simplifyPoints(points, epsilon, start, maxNdx + 1, outPoints);
            simplifyPoints(points, epsilon, maxNdx, end, outPoints);
        } else {
            outPoints.add(s);
            outPoints.add(e);
        }

        return outPoints.toArray(new Double[0][0]);
    }

    public void destroy() {
        for (VertexCache cache : this.charMap.values()) {
            cache.destroy();
        }
        this.charMap.clear();
    }

    public void preGlHint() {
        GlStateManager.enableColorMaterial();
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glHint(3155, 4353);
        GL11.glEnable(2881);
        GL11.glDisable(2884);
    }

    public void postGlHint() {
        GL11.glDisable(2881);
        GL11.glEnable(2884);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public Font getFont() {
        return this.font;
    }
}
