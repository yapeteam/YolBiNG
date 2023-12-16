package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventPostMotion;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.*;
import cn.yapeteam.yolbi.util.animation.AnimationHolder;
import cn.yapeteam.yolbi.util.animation.AnimationType;
import cn.yapeteam.yolbi.util.animation.AnimationUtil;
import cn.yapeteam.yolbi.util.render.DrawUtil;
import cn.yapeteam.yolbi.util.render.FontUtil;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("DuplicatedCode")
@ModuleInfo(name = "Module List", category = ModuleCategory.VISUAL)
public class ModuleList extends HUDModule {
    private boolean initialised;

    private final ArrayList<AnimationHolder<Module>> modules = new ArrayList<>();

    private final ModeValue<String> mode = new ModeValue<>("Mode", "Simple", "Simple", "New", "Outline", "Bloom", "Custom");
    private final BooleanValue translate = new BooleanValue("translate", true);

    private final ModeValue<String> font = FontUtil.getFontSetting(() -> mode.is("Custom"));

    private final ModeValue<AnimationType> animType = AnimationUtil.getAnimationType(() -> mode.is("Custom"), AnimationType.POP);
    private final NumberValue<Integer> animDuration = AnimationUtil.getAnimationDuration(() -> mode.is("Custom"), 250);
    private final NumberValue<Integer> bloom = new NumberValue<>("Bloom",()->mode.is("Bloom"),10,0,100,1);

    private final NumberValue<Double> verticalSpacing = new NumberValue<>("Vertical spacing", () -> mode.is("Custom"), 10.5, 8.0, 20.0, 0.5);

    private final NumberValue<Double> extraWidth = new NumberValue<>("Extra width", () -> mode.is("Custom"), 0.5, 0.0, 6.0, 0.5);

    private final BooleanValue box = new BooleanValue("Box", () -> mode.is("Custom"), false);
    private final NumberValue<Integer> boxAlpha = new NumberValue<>("Box alpha", () -> mode.is("Custom") && box.getValue(), 100, 5, 255, 5);

    private final BooleanValue leftOutline = new BooleanValue("Left outline", () -> mode.is("Custom"), false);
    private final BooleanValue rightOutline = new BooleanValue("Right outline", () -> mode.is("Custom"), false);
    private final BooleanValue topOutline = new BooleanValue("Top outline", () -> mode.is("Custom"), false);
    private final BooleanValue bottomOutline = new BooleanValue("Bottom outline", () -> mode.is("Custom"), false);

    private final ModeValue<AlignType> alignMode = new ModeValue<>("Align type", AlignType.RIGHT, AlignType.values());

    private AbstractFontRenderer productSans;

    private ClientTheme theme;

    public ModuleList() {
        super(5, 5, 100, 200, AlignType.RIGHT);
        this.addValues(mode, font, translate, animType, animDuration, verticalSpacing, box, extraWidth, boxAlpha, leftOutline, rightOutline, topOutline, bottomOutline, alignMode,bloom);
        this.listenType = EventListenType.MANUAL;
        this.startListening();
        this.setEnabledSilently(true);
    }

    @Override
    public void onClientStarted() {
        YolBi.instance.getModuleManager().modules.forEach(m -> modules.add(new AnimationHolder<>(m)));

        productSans = YolBi.instance.getFontManager().getProductSans();

        theme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);
    }

    @Override
    protected void renderModule(boolean inChat) {
        if (!initialised) {
            sort();
            initialised = true;
        }

        alignType = alignMode.getValue();

        if (mc.gameSettings.showDebugInfo) return;

        switch (mode.getValue()) {
            case "Simple":
                renderSimple();
                break;
            case "New":
                renderNew();
                break;
            case "Outline":
                renderOutline();
                break;
            case "Bloom":
                renderBloom();
                break;
            case "Custom":
                renderCustom();
                break;
        }
    }

    @Listener
    public void onPostMotion(EventPostMotion event) {
        sort();
    }

    private void sort() {
        Collections.reverse(modules);

        modules.sort((m1, m2) -> (int) (Math.round((getStringWidth(m1.get().getName()) * 8) - Math.round(getStringWidth(m2.get().getName()) * 8))));

        Collections.reverse(modules);
    }

    private void renderSimple() {
        ScaledResolution sr = new ScaledResolution(mc);

        float x = posX.getValue().floatValue();
        float y = posY.getValue().floatValue();

        float offsetY = 10.5F;

        float width = 0;

        for (AnimationHolder<Module> holder : modules) {
            Module m = holder.get();
            String name = m.getName();

            float startX = alignMode.getValue() == AlignType.LEFT ? x : (float) (sr.getScaledWidth() - getStringWidth(name) - x);
            float startY = y;

            float endX = alignMode.getValue() == AlignType.LEFT ? (float) (x + getStringWidth(name)) : sr.getScaledWidth() - x;
            float endY = y + offsetY;

            if (Math.abs(endX - startX) > width) {
                width = Math.abs(endX - startX);
            }

            holder.setAnimType(AnimationType.POP2);
            holder.setAnimDuration(250);
            holder.updateState(m.isEnabled());
            if (!holder.isAnimDone() || holder.isRendered()) {
                holder.render(() -> drawStringWithShadow(name, startX, startY, getColor((int) (startY * -17))), startX, startY, endX, endY);
                y += offsetY * holder.getYMult();
            }
        }

        this.width = (int) (width) + 1;
        this.height = (int) (y - posY.getValue().intValue()) + 1;
    }

    private void renderBloom() {
        ScaledResolution sr = new ScaledResolution(mc);

        float x = posX.getValue().floatValue();
        float y = posY.getValue().floatValue();

        float offsetY = 10.5F;

        float width = 0;

        for (AnimationHolder<Module> holder : modules) {
            Module m = holder.get();

            holder.setAnimType(AnimationType.SLIDE);
            holder.setAnimDuration(350);
            holder.updateState(m.isEnabled());

            if (!holder.isAnimDone() || holder.isRendered()) {
                String name = m.getName();
                double nameLength = getStringWidth(name);

                float mult = holder.getYMult();

                float startX = alignMode.getValue() == AlignType.LEFT ? x - 5 : (float) (sr.getScaledWidth() - nameLength - x - 5);
                float startY = y;

                float endX = alignMode.getValue() == AlignType.LEFT ? (float) (x + nameLength) : sr.getScaledWidth() - x;
                float endY = y + offsetY;

                if (Math.abs(endX - startX) > width) {
                    width = Math.abs(endX - startX);
                }

                holder.render(() -> {
                    //RenderUtil.drawBloomShadow();
                    //Gui.drawRect(startX, startY, startX + 2, endY, getColor((int) (startY * -17)));
                    Color alColor = new Color(getColor((int) (startY * -17)));
                    RenderUtil.drawBloomShadow(startX + 2, startY, endX - startX - 2, endY - startY, bloom.getValue(),
                            new Color(alColor.getRed(), alColor.getGreen(), alColor.getBlue(), 200)
                    );
                    //Gui.drawRect(startX + 2, startY, endX, endY, 0x70000000);
                    drawStringWithShadow(name, startX + 3.5F, startY + 1.5F, getColor((int) (startY * -17)));
                }, startX, startY, endX, startY + offsetY * mult);

                y += offsetY * Math.min(mult * 4, 1);
            }
        }

        this.width = (int) (width) + 1;
        this.height = (int) (y - posY.getValue().intValue()) + 1;
    }

    private void renderNew() {
        ScaledResolution sr = new ScaledResolution(mc);

        float x = posX.getValue().floatValue();
        float y = posY.getValue().floatValue();

        float offsetY = 10.5F;

        float width = 0;

        for (AnimationHolder<Module> holder : modules) {
            Module m = holder.get();

            holder.setAnimType(AnimationType.SLIDE);
            holder.setAnimDuration(350);
            holder.updateState(m.isEnabled());

            if (!holder.isAnimDone() || holder.isRendered()) {
                String name = m.getName();
                double nameLength = getStringWidth(name);

                float mult = holder.getYMult();

                float startX = alignMode.getValue() == AlignType.LEFT ? x - 5 : (float) (sr.getScaledWidth() - nameLength - x - 5);
                float startY = y;

                float endX = alignMode.getValue() == AlignType.LEFT ? (float) (x + nameLength) : sr.getScaledWidth() - x;
                float endY = y + offsetY;

                if (Math.abs(endX - startX) > width) {
                    width = Math.abs(endX - startX);
                }

                holder.render(() -> {
                    Gui.drawRect(startX, startY, startX + 2, endY, getColor((int) (startY * -17)));
                    Gui.drawRect(startX + 2, startY, endX, endY, 0x70000000);
                    drawStringWithShadow(name, startX + 3.5F, startY + 1.5F, getColor((int) (startY * -17)));
                }, startX, startY, endX, startY + offsetY * mult);

                y += offsetY * Math.min(mult * 4, 1);
            }
        }

        this.width = (int) (width) + 1;
        this.height = (int) (y - posY.getValue().intValue()) + 1;
    }

    private void renderOutline() {
        ScaledResolution sr = new ScaledResolution(mc);

        float x = posX.getValue().floatValue();
        float y = posY.getValue().floatValue();

        float offsetY = 11F;

        float lastStartX = 0, lastEndX = 0;

        boolean firstModule = true;

        float width = 0;

        for (AnimationHolder<Module> holder : modules) {
            Module m = holder.get();
            String name = m.getName();

            holder.setAnimType(AnimationType.POP2);
            holder.setAnimDuration(250);
            holder.updateState(m.isEnabled());

            if (!holder.isAnimDone() || holder.isRendered()) {
                float startX = alignMode.getValue() == AlignType.LEFT ? x : (float) (sr.getScaledWidth() - getStringWidth(name) - x);
                float startY = y;

                float endX = alignMode.getValue() == AlignType.LEFT ? (float) (x + getStringWidth(name)) : sr.getScaledWidth() - x;
                float endY = y + offsetY;

                if (Math.abs(endX - startX) > width) {
                    width = Math.abs(endX - startX);
                }

                if (firstModule) {
                    DrawUtil.drawGradientVerticalRect(startX - 1.5, y - 2, endX + 1.5, y, 0, 0x50000000);
                } else {
                    double diff = (startX - 3.5) - (lastStartX - 1.5);

                    if (diff > 1) {
                        DrawUtil.drawGradientVerticalRect(lastStartX - 1.5, y, startX - 3, y + 2, 0x50000000, 0);
                    }
                }

                DrawUtil.drawGradientSideRect(endX + 1.5, startY, endX + 3.5, startY + offsetY * holder.getYMult(), 0x50000000, 0);

                holder.render(() -> {
                    DrawUtil.drawGradientSideRect(startX - 3.5, startY, startX - 1.5, endY, 0, 0x50000000);

                    drawStringWithShadow(name, startX, startY + 2, getColor((int) (startY * -17)));
                }, startX - 3.5F, startY, endX, endY);

                y += offsetY * holder.getYMult();

                lastStartX = startX;
                lastEndX = endX;

                firstModule = false;
            }
        }

        DrawUtil.drawGradientVerticalRect(lastStartX - 1.5, y, lastEndX + 1.5, y + 2, 0x50000000, 0);

        this.width = (int) (width) + 1;
        this.height = (int) (y - posY.getValue().intValue()) + 1;
    }

    private void renderCustom() {
        ScaledResolution sr = new ScaledResolution(mc);

        float x = posX.getValue().floatValue();
        float y = posY.getValue().floatValue();

        float offsetY = verticalSpacing.getValue().floatValue();
        double extraWidth = this.extraWidth.getValue();

        float width = 0;

        float space = offsetY - getFontHeight();

        boolean firstModule = true;

        float lastStartX = 0, lastEndX = 0;

        for (AnimationHolder<Module> holder : modules) {
            Module m = holder.get();
            String name = m.getName();

            holder.setAnimType(animType.getValue());
            holder.setAnimDuration(animDuration.getValue());
            holder.updateState(m.isEnabled());

            if (!holder.isAnimDone() || holder.isRendered()) {
                float startX = alignMode.getValue() == AlignType.LEFT ? x : (float) (sr.getScaledWidth() - getStringWidth(name) - x);
                float startY = y;

                float endX = alignMode.getValue() == AlignType.LEFT ? (float) (x + getStringWidth(name)) : sr.getScaledWidth() - x;

                if (Math.abs(endX - startX) > width) {
                    width = Math.abs(endX - startX);
                }

                float mult = holder.getYMult();

                if (leftOutline.getValue()) {
                    Gui.drawRect(startX - 2 - extraWidth, startY, startX - extraWidth, startY + offsetY * mult, getColor((int) (startY * -17)));
                }

                if (rightOutline.getValue()) {
                    Gui.drawRect(endX + extraWidth, startY, endX + extraWidth + 2, startY + offsetY * mult, getColor((int) (startY * -17)));
                }

                if (firstModule) {
                    if (topOutline.getValue()) {
                        double left = leftOutline.getValue() ? startX - extraWidth - 2 : startX - extraWidth;
                        double right = rightOutline.getValue() ? endX + extraWidth + 2 : endX + extraWidth;

                        Gui.drawRect(left, startY - 2, right, startY, getColor((int) (startY * -17)));
                    }
                } else {
                    if (bottomOutline.getValue()) {
                        double left = leftOutline.getValue() ? lastStartX - extraWidth - 2 : lastStartX - extraWidth;
                        double right = leftOutline.getValue() ? startX - extraWidth - 2 : startX - extraWidth;

                        Gui.drawRect(left, startY, right, startY + 2, getColor((int) (startY * -17)));
                    }
                }

                if (box.getValue()) {
                    Gui.drawRect(startX - extraWidth, startY, endX + extraWidth, startY + offsetY * mult, new Color(0, 0, 0, boxAlpha.getValue()).getRGB());
                }

                y += offsetY * mult;

                firstModule = false;
                lastStartX = startX;
                lastEndX = endX;
            }
        }

        if (bottomOutline.getValue() && !firstModule) {
            double left = leftOutline.getValue() ? lastStartX - extraWidth - 2 : lastStartX - extraWidth;
            double right = rightOutline.getValue() ? lastEndX + extraWidth + 2 : lastEndX + extraWidth;

            Gui.drawRect(left, y, right, y + 2, getColor((int) (y * -17)));
        }

        y = posY.getValue().floatValue();

        for (AnimationHolder<Module> holder : modules) {
            Module m = holder.get();
            String name = m.getName();

            if (!holder.isAnimDone() || holder.isRendered()) {
                float startX = alignMode.getValue() == AlignType.LEFT ? x : (float) (sr.getScaledWidth() - getStringWidth(name) - x);
                float startY = y;

                float endX = alignMode.getValue() == AlignType.LEFT ? (float) (x + getStringWidth(name)) : sr.getScaledWidth() - x;
                float endY = y + offsetY;

                float mult = holder.getYMult();

                float renderY = startY + Math.round(space + 1) / 2F;

                holder.render(() -> drawStringWithShadow(name, startX, renderY, getColor((int) (startY * -17))), startX, startY, endX, endY);
                y += offsetY * mult;
            }
        }

        this.width = (int) (width) + 1;
        this.height = (int) (y - posY.getValue().intValue()) + 1;
    }

    public void drawString(String text, float x, float y, int color) {
        text = translate.getValue() ? YolBi.instance.getLanguageManager().translate(text) : text;
        switch (mode.getValue()) {
            case "Simple":
                mc.fontRendererObj.drawString(text, x, y, color);
                return;
            case "New":
            case "Bloom":
            case "Outline":
                productSans.drawString(text, x, y, color);
                return;
            case "Custom":
                FontUtil.drawString(font.getValue(), text, x, y, color);
                break;
        }

        mc.fontRendererObj.drawString(text, x, y, color);
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        text = translate.getValue() ? YolBi.instance.getLanguageManager().translate(text) : text;
        switch (mode.getValue()) {
            case "Simple":
            case "New":
            case "Bloom":
                mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
                break;
            case "Outline":
                productSans.drawStringWithShadow(text, x, y, color);
                break;
            case "Custom":
                FontUtil.drawStringWithShadow(font.getValue(), text, x, y, color);
                break;
        }
    }

    public double getStringWidth(String text) {
        text = translate.getValue() ? YolBi.instance.getLanguageManager().translate(text) : text;
        switch (mode.getValue()) {
            case "Simple":
                return mc.fontRendererObj.getStringWidth(text);
            case "New":
            case "Bloom":
            case "Outline":
                return productSans.getStringWidth(text);
            case "Custom":
                return FontUtil.getStringWidth(font.getValue(), text);
        }

        return mc.fontRendererObj.getStringWidth(text);
    }

    public float getFontHeight() {
        switch (mode.getValue()) {
            case "Simple":
                return mc.fontRendererObj.FONT_HEIGHT;
            case "New":
            case "Bloom":
            case "Outline":
                return productSans.getHeight();
            case "Custom":
                return FontUtil.getFontHeight(font.getValue());
        }

        return mc.fontRendererObj.FONT_HEIGHT;
    }

    public int getColor(int offset) {
        return theme.getColor(offset);
    }


}