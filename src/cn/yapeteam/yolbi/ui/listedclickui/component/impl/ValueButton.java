package cn.yapeteam.yolbi.ui.listedclickui.component.impl;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.ui.Theme;
import cn.yapeteam.yolbi.ui.listedclickui.ImplScreen;
import cn.yapeteam.yolbi.ui.listedclickui.component.AbstractComponent;
import cn.yapeteam.yolbi.ui.listedclickui.component.Limitation;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.values.Value;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ColorValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.Arrays;

/**
 * @author TIMER_err
 */
public class ValueButton extends AbstractComponent {
    @Getter
    private final Value<?> value;

    public ValueButton(AbstractComponent parent, Value<?> value) {
        super(parent);
        this.value = value;
    }

    private float sliderAnimeWidth = 0;

    @Override
    public void update() {
        if (!((ModuleButton) getParent()).isExtended() || !value.getVisibility().get()) sliderAnimeWidth = 0;
        super.update();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks, Limitation limitation) {
        if (!(
                getX() + getWidth() < limitation.getX() ||
                getX() > limitation.getX() + limitation.getWidth() ||
                getY() + getHeight() < limitation.getY() ||
                getY() > limitation.getY() + limitation.getHeight()
        )) {
            GlStateManager.color(1, 1, 1, 1);
            RenderUtil.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Theme.MainTheme[1].darker().getRGB());
            AbstractFontRenderer font = Vestige.instance.getFontManager().getPingFang12();
            AbstractFontRenderer icon = Vestige.instance.getFontManager().getFLUXICON14();
            int index = 0, all = 0;
            for (AbstractComponent component : getParent().getParent().getChildComponents()) {
                if (component instanceof ModuleButton) {
                    ModuleButton moduleButton = (ModuleButton) component;
                    boolean should = getParent().getParent().getChildComponents().indexOf(getParent()) > getParent().getParent().getChildComponents().indexOf(moduleButton);
                    all++;
                    if (should)
                        index++;
                    if (moduleButton.isExtended())
                        for (AbstractComponent childComponent : moduleButton.getChildComponents())
                            if (childComponent instanceof ValueButton) {
                                all++;
                                if (should)
                                    index++;
                                else if (moduleButton == getParent() && getParent().getChildComponents().indexOf(this) >= getParent().getChildComponents().indexOf(childComponent))
                                    index++;
                            }
                }
            }
            Color rainbow = ColorUtil.rainbow(10, (all - 1 - index) * 10, 1, 1, 1);
            if (value instanceof BooleanValue) {
                BooleanValue booleanValue = (BooleanValue) value;
                font.drawString(value.getName(), getX() + 5, getY() + (getHeight() - font.getHeight()) / 2f, -1);
                int w = 8, h = 8;
                RenderUtil.drawRect2(getX() + getWidth() - 5 - w, getY() + (getHeight() - h) / 2f, w, h, Theme.MainTheme[1].getRGB());
                if (booleanValue.getValue())
                    icon.drawString("v", getX() + getWidth() - 5 - w + 0.5f, getY() + (getHeight() - icon.getHeight()) / 2f, ImplScreen.rainbow ? rainbow.getRGB() : Theme.MainTheme[2].getRGB());
            } else if (value instanceof NumberValue<?>) {
                NumberValue<?> numberValue = (NumberValue<?>) value;
                font.drawString(numberValue.getName(), getX() + 5, getY() + 3, -1);
                font.drawString(String.format("%.2f", numberValue.getValue().floatValue()), getX() + getWidth() - font.getStringWidth(String.format("%.2f", numberValue.getValue().floatValue())) - 5, getY() + 3, -1);
                float w = (getWidth() - 10) * ((numberValue.getValue().floatValue() - numberValue.getMin().floatValue()) / (numberValue.getMax().floatValue() - numberValue.getMin().floatValue()));
                sliderAnimeWidth += (w - sliderAnimeWidth) / 10f;
                RenderUtil.drawRect2(getX() + 5, getY() + getHeight() - 5 - 1, getWidth() - 10, 1, Theme.MainTheme[3].getRGB());
                RenderUtil.drawRect2(getX() + 5, getY() + getHeight() - 5 - 1, sliderAnimeWidth, 1, ImplScreen.rainbow ? rainbow.getRGB() : Theme.MainTheme[2].getRGB());
                RenderUtil.drawRect2(getX() + 5 + sliderAnimeWidth - 4, getY() + getHeight() - 5 - 1, 8, 1, Theme.MainTheme[1].darker().getRGB());
                RenderUtil.circle(getX() + 5 + sliderAnimeWidth, getY() + getHeight() - 5 - 1 + 0.5f, 2.5f, ImplScreen.rainbow ? rainbow.getRGB() : Theme.MainTheme[2].getRGB());

                if (isDragging()) {
                    if (mouseX >= getX() + 5 && mouseX <= getX() + 5 + getWidth() - 10) {
                        double val = (mouseX - (getX() + 5)) / (getWidth() - 10) * (numberValue.getMax().floatValue() - numberValue.getMin().floatValue()) + numberValue.getMin().floatValue();
                        numberValue.setValue(val - val % numberValue.getInc().doubleValue());
                    } else if (mouseX < getX() + 5)
                        numberValue.setValue(numberValue.getMin());
                    else if (mouseX > getWidth() - 10)
                        numberValue.setValue(numberValue.getMax());
                }
            } else if (value instanceof ModeValue<?>) {
                ModeValue<?> modeValue = (ModeValue<?>) value;
                RenderUtil.drawFastRoundedRect(getX() + 2, getY() + 2, getX() + getWidth() - 2, getY() + getHeight() - 2, 2, Theme.MainTheme[1].getRGB());
                font.drawString(modeValue.getName() + " | " + modeValue.getValue(), getX() + (getWidth() - font.getStringWidth(modeValue.getName() + " | " + modeValue.getValue())) / 2f, getY() + (getHeight() - font.getHeight()) / 2f - 3, -1);
                font.drawString("|", getX() + (getWidth() - font.getStringWidth("|")) / 2f, getY() + getHeight() / 2f - 0.5f, ImplScreen.rainbow ? rainbow.getRGB() : Theme.MainTheme[2].getRGB());
                icon.drawString("h i", getX() + (getWidth() - icon.getStringWidth("h i")) / 2f, getY() + getHeight() / 2f + 3, ImplScreen.rainbow ? rainbow.getRGB() : Theme.MainTheme[2].getRGB());
            } else if (value instanceof ColorValue) {
                ColorValue colorValue = (ColorValue) value;
                font.drawString(colorValue.getName() + ":", getX() + (getWidth() - font.getStringWidth(colorValue.getName() + ":") - 2 - 5) / 2f, getY() + 2 - 3, ImplScreen.rainbow ? rainbow.getRGB() : Theme.MainTheme[2].getRGB());
                RenderUtil.drawFastRoundedRect2(getX() + (getWidth() - font.getStringWidth(colorValue.getName() + ":") - 2 - 5) / 2f + font.getStringWidth(colorValue.getName() + ":") + 2, getY() + 2, 5, 5, 1, colorValue.getColor());
                colorValue.draw(getX() + (getWidth() - 54) / 2f, getY() + 9, 40, 40, mouseX, mouseY);
            }
        } else sliderAnimeWidth = 0;
        super.drawComponent(mouseX, mouseY, partialTicks, limitation);
    }

    public void setHeight() {
        if (value instanceof BooleanValue)
            setHeight(12);
        else if (value instanceof NumberValue)
            setHeight(22);
        else if (value instanceof ModeValue)
            setHeight(20);
        else if (value instanceof ColorValue)
            setHeight(52);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (isHovering(getParent().getParent().getX(), getParent().getParent().getY() + ImplScreen.panelTopHeight, getParent().getParent().getWidth(), getParent().getParent().getHeight() - ImplScreen.panelTopHeight, mouseX, mouseY))
            if (isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY)) {
                if (value instanceof BooleanValue) {
                    BooleanValue booleanValue = (BooleanValue) value;
                    booleanValue.setValue(!booleanValue.getValue());
                } else if (value instanceof NumberValue) {
                    if (isHovering(getX() + 2, getY() + getHeight() - 5 - 4, getWidth() - 4, 7, mouseX, mouseY))
                        setDragging(true);
                } else if (value instanceof ModeValue<?>) {
                    ModeValue<?> modeValue = (ModeValue<?>) value;
                    int index = Arrays.asList(modeValue.getModes()).indexOf(modeValue.getValue());
                    if (isHovering(getX(), getY(), getWidth() / 2f, getHeight(), mouseX, mouseY))
                        modeValue.setValue(modeValue.getModes()[index > 0 ? index - 1 : modeValue.getModes().length - 1].toString());
                    if (isHovering(getX() + getWidth() / 2f, getY(), getWidth() / 2f, getHeight(), mouseX, mouseY))
                        modeValue.setValue(modeValue.getModes()[index < modeValue.getModes().length - 1 ? index + 1 : 0].toString());
                }
            }
    }
}
